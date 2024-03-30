package org.lexize.fsb;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                "CREATE TABLE IF NOT EXISTS %s_USERDATA (id VARCHAR(16), badges VARCHAR(4), PRIMARY KEY(id))"
                .formatted(tablePrefix));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_EQUIPPED_AVATARS (id VARCHAR(32), hash VARCHAR(64), PRIMARY KEY(id, hash))"
                .formatted(tablePrefix));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_AVATARS (hash VARCHAR(64), id VARCHAR(255), owner VARCHAR(32), data LONGBLOB, PRIMARY KEY (hash, id, owner))"
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

    public void uploadAvatar(String hash, String owner, String id, byte[] data) throws SQLException {
        PreparedStatement statement;
        if (avatarExists(owner, id)) {
            statement = conn.prepareStatement("UPDATE %s_AVATARS SET hash = ?, data = ? WHERE owner = ? AND id = ?".formatted(tablePrefix));
            statement.setString(1, hash);
            statement.setBytes(2, data);
            statement.setString(3, owner);
            statement.setString(4, id);
        }
        else {
            statement = conn.prepareStatement("INSERT INTO %s_AVATARS(hash, data, owner, id) VALUES (?, ?, ?, ?)".formatted(tablePrefix));
            statement.setString(1, hash);
            statement.setBytes(2, data);
            statement.setString(3, owner);
            statement.setString(4, id);
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
