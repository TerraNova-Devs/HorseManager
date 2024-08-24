package de.mcterranova.horseManager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPDatabase {

    private static HikariCPDatabase instance;
    private HikariDataSource dataSource;

    private HikariCPDatabase() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/horse_manager");  // Passe die URL, den Benutzernamen und das Passwort an
        config.setUsername("minecraft");
        config.setPassword("minecraft");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        dataSource = new HikariDataSource(config);
        prepareTables();
    }

    public static synchronized HikariCPDatabase getInstance() throws SQLException {
        if (instance == null) {
            instance = new HikariCPDatabase();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void prepareTables() throws SQLException {
        try (Connection connection = getConnection()) {
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS horse_data (" +
                            "player_uuid VARCHAR(36) PRIMARY KEY," +
                            "horse_name VARCHAR(255)," +
                            "health DOUBLE," +
                            "jump_strength DOUBLE," +
                            "movement_speed DOUBLE," +
                            "color VARCHAR(255)," +
                            "style VARCHAR(255)," +
                            "saddle BLOB," +
                            "armor BLOB)"
            );
        }
    }

    public void closeConnection() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
