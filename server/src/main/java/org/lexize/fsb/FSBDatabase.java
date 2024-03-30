package org.lexize.fsb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class FSBDatabase {
    private final Connection conn;
    public FSBDatabase(FSBConfig config) throws SQLException {
        if (config.getDatabaseLogin() != null) {
            conn = DriverManager.getConnection(
                    config.getDatabaseConnectionString(),
                    config.getDatabaseLogin(),
                    config.getDatabasePassword()
            );
        }
        else conn = DriverManager.getConnection(config.getDatabaseConnectionString());
        var statement = conn.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_USERDATA (id VARCHAR(16), badges VARCHAR(4), PRIMARY KEY(id))"
                .formatted(config.getDatabaseTablePrefix()));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_EQUIPPED_AVATARS (id VARCHAR(32), hash VARCHAR(64), PRIMARY KEY(id, hash))"
                .formatted(config.getDatabaseTablePrefix()));
        statement.execute(
                "CREATE TABLE IF NOT EXISTS %s_AVATARS (hash VARCHAR(64), id VARCHAR(255), owner VARCHAR(32), data LONGBLOB, PRIMARY KEY (hash))"
                .formatted(config.getDatabaseTablePrefix()));
        statement.close();
    }
}
