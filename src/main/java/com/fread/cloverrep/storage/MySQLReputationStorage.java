package com.fread.cloverrep.storage;

import com.fread.cloverrep.Main;
import com.fread.cloverrep.managers.ConfigManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class MySQLReputationStorage implements ReputationDataSource {

    private final Main plugin;
    private Connection connection;

    private final String tablePlayers;
    private final String tableVoters;

    public MySQLReputationStorage(Main plugin) {
        this.plugin = plugin;

        // Считываем настройки из ConfigManager
        ConfigManager cfg = plugin.getConfigManager();
        String host = cfg.getMySQLHost();
        int port = cfg.getMySQLPort();
        String database = cfg.getMySQLDatabase();
        String user = cfg.getMySQLUser();
        String password = cfg.getMySQLPassword();
        String prefix = cfg.getMySQLTablePrefix();

        this.tablePlayers = prefix + "players";
        this.tableVoters = prefix + "voters";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Важно для MySQL 8+ (часто не требуется, но оставим)
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=UTF-8",
                    user,
                    password
            );
            plugin.getLogger().info("Подключение к MySQL успешно!");

            createTables();
        } catch (Exception e) {
            plugin.getLogger().severe("Не удалось подключиться к MySQL!");
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        // Таблица для хранения репутаций
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablePlayers + " ("
                    + "player_name VARCHAR(36) NOT NULL PRIMARY KEY,"
                    + "reputation INT NOT NULL DEFAULT 0"
                    + ")");
        }
        // Таблица для хранения того, кто за кого голосовал
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableVoters + " ("
                    + "voter VARCHAR(36) NOT NULL,"
                    + "target VARCHAR(36) NOT NULL,"
                    + "PRIMARY KEY (voter, target)"
                    + ")");
        }
    }

    @Override
    public int getReputation(String playerName) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT reputation FROM " + tablePlayers + " WHERE player_name = ?"
        )) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reputation");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setReputation(String playerName, int reputation) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tablePlayers + " (player_name, reputation) VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE reputation = VALUES(reputation)"
        )) {
            ps.setString(1, playerName);
            ps.setInt(2, reputation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasVoted(String voter, String target) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + tableVoters + " WHERE voter = ? AND target = ?"
        )) {
            ps.setString(1, voter);
            ps.setString(2, target);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addVoter(String voter, String target) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableVoters + " (voter, target) VALUES (?, ?)"
        )) {
            ps.setString(1, voter);
            ps.setString(2, target);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getVoters(String playerName) {
        Set<String> result = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT target FROM " + tableVoters + " WHERE voter = ?"
        )) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("target"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void saveData() {
        // Для MySQL обычно не нужно вручную сохранять,
        // так как данные пишутся сразу. Оставим метод пустым.
    }
}

// .