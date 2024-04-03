package org.lexize.fsb;

import org.lexize.fsb.utils.Pair;
import org.lexize.fsb.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FSBDatabase {
    private final Connection conn;
    private final String tablePrefix;
    public FSBDatabase(FSBConfig config) throws SQLException {
        if (config.getDatabaseLogin() != null) {
            conn = DriverManager.getConnection(
                    config.getDatabaseConnectionString(),
                    config.getDatabaseLogin(),
                    config.getDatabasePassword()
            );
        }
        else conn = DriverManager.getConnection(config.getDatabaseConnectionString());
        tablePrefix = config.getDatabaseTablePrefix();
        var statement = conn.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_USERDATA (id VARCHAR(32), badges VARCHAR(4), PRIMARY KEY(id))"
                .formatted(tablePrefix));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_EQUIPPED_AVATARS (id VARCHAR(32), hash VARCHAR(64), PRIMARY KEY(id, hash))"
                .formatted(tablePrefix));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_AVATARS (hash VARCHAR(64), ehash VARCHAR(64), id VARCHAR(255), owner VARCHAR(32), data LONGBLOB, PRIMARY KEY (id, owner))"
                .formatted(tablePrefix));
        statement.close();
    }

    public byte[] getAvatarData(String hash) throws SQLException {
        var statement = conn.prepareStatement("SELECT data FROM %s_AVATARS WHERE hash = ?".formatted(tablePrefix));
        statement.setString(1, hash);
        var result = statement.executeQuery();
        if (result.next()) {
            byte[] data = result.getBytes(1);
            result.close();
            statement.close();
            return data;
        }
        return null;
    }

    public Pair<UUID, String> getAvatarIdAndOwner(String hash) throws SQLException {
        var statement = conn.prepareStatement("SELECT owner, id FROM %s_AVATARS WHERE hash = ?".formatted(tablePrefix));
        statement.setString(1, hash);
        var result = statement.executeQuery();
        if (result.next()) {
            UUID owner = UUID.fromString(result.getString(1));
            String id = result.getString(2);
            result.close();
            statement.close();
            return new Pair<>(owner, id);
        }
        return null;
    }

    public Pair<String, String> getAvatarHash(UUID owner, String id) throws SQLException {
        var statement = conn.prepareStatement("SELECT hash, ehash FROM %s_AVATARS WHERE owner = ? AND id = ?".formatted(tablePrefix));
        statement.setString(1, Utils.uuidToHex(owner));
        statement.setString(2, id);
        var result = statement.executeQuery();
        if (result.next()) {
            String hash = result.getString(1);
            String ehash = result.getString(2);
            result.close();
            statement.close();
            return new Pair<>(hash, ehash);
        }
        return null;
    }

    public String getAvatarEHash(String hash) throws SQLException {
        var statement = conn.prepareStatement("SELECT ehash FROM %s_AVATARS WHERE hash = ?".formatted(tablePrefix));
        statement.setString(1, hash);
        var result = statement.executeQuery();
        if (result.next()) {
            String ehash = result.getString(2);
            result.close();
            statement.close();
            return ehash;
        }
        return null;
    }

    public Pair<byte[], String> getAvatarData(String owner, String id) throws SQLException {
        var statement = conn.prepareStatement("SELECT data, hash FROM %s_AVATARS WHERE owner = ? AND id = ?".formatted(tablePrefix));
        statement.setString(1, owner);
        statement.setString(1, id);
        var result = statement.executeQuery();
        if (result.next()) {
            byte[] data = result.getBytes(1);
            String hash = result.getString(2);
            result.close();
            statement.close();
            return new Pair<>(data, hash);
        }
        return null;
    }

    public boolean avatarExists(String hash) throws SQLException {
        var statement = conn.prepareStatement("SELECT count(*) FROM %s_AVATARS WHERE hash = ?".formatted(tablePrefix));
        statement.setString(1, hash);
        var result = statement.executeQuery();
        if (result.next()) {
            int count = result.getInt(1);
            result.close();
            statement.close();
            return count > 0;
        }
        return false;
    }

    public boolean avatarExists(String owner, String id) throws SQLException {
        var statement = conn.prepareStatement("SELECT count(*) FROM %s_AVATARS WHERE owner = ? AND id = ?".formatted(tablePrefix));
        statement.setString(1, owner);
        statement.setString(2, id);
        var result = statement.executeQuery();
        if (result.next()) {
            int count = result.getInt(1);
            result.close();
            statement.close();
            return count > 0;
        }
        return false;
    }

    public List<String> ownedAvatars(String owner) throws SQLException {
        var statement = conn.prepareStatement("SELECT count(id) FROM %s_AVATARS WHERE owner = ?".formatted(tablePrefix));
        statement.setString(1, owner);
        var result = statement.executeQuery();
        ArrayList<String> avatars = new ArrayList<>();
        while (result.next()) {
            avatars.add(result.getString(1));
        }
        result.close();
        statement.close();
        return avatars;
    }

    public int ownedAvatarsCount(String owner) throws SQLException {
        return ownedAvatars(owner).size();
    }

    public void uploadAvatar(String hash, String ehash, String owner, String id, byte[] data) throws SQLException {
        PreparedStatement statement;
        if (avatarExists(owner, id)) {
            statement = conn.prepareStatement("UPDATE %s_AVATARS SET hash = ?, ehash = ?, data = ? WHERE owner = ? AND id = ?".formatted(tablePrefix));
            statement.setString(1, hash);
            statement.setString(2, ehash);
            statement.setBytes(3, data);
            statement.setString(4, owner);
            statement.setString(5, id);
        }
        else {
            statement = conn.prepareStatement("INSERT INTO %s_AVATARS(hash, ehash, data, owner, id) VALUES (?, ?, ?, ?, ?)".formatted(tablePrefix));
            statement.setString(1, hash);
            statement.setString(2, ehash);
            statement.setBytes(3, data);
            statement.setString(4, owner);
            statement.setString(5, id);
        }
        statement.execute();
    }

    public boolean deleteAvatar(String owner, String avatarId) throws SQLException {
        var statement = conn.prepareStatement("DELETE FROM %s_AVATARS where owner = ? AND id = ?".formatted(tablePrefix));
        statement.setString(1, owner);
        statement.setString(2, avatarId);
        return statement.executeUpdate() > 0;
    }
}
