package com.fread.cloverrep;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final Main plugin;
    private final FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    // Тип хранилища (local или mysql)
    public String getStorageType() {
        return config.getString("storage.type", "local").toLowerCase();
    }

    // Данные для подключения к MySQL
    public String getMySQLHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("mysql.database", "minecraft");
    }

    public String getMySQLUser() {
        return config.getString("mysql.user", "root");
    }

    public String getMySQLPassword() {
        return config.getString("mysql.password", "");
    }

    public String getMySQLTablePrefix() {
        return config.getString("mysql.tablePrefix", "clover_");
    }

    // Цвета
    public String getPositiveReputationColor() {
        return config.getString("colors.reputation-positive", "&#00FF00");
    }

    public String getNegativeReputationColor() {
        return config.getString("colors.reputation-negative", "&#FF0000");
    }

    public String getNeutralReputationColor() {
        return config.getString("colors.reputation-neutral", "&#AAAAAA");
    }

    // Сообщения
    public String getAlreadyVotedMessage() {
        return ColorUtil.applyColor(config.getStringList("messages.already-voted")
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""));
    }

    public String getSelfVoteErrorMessage() {
        return ColorUtil.applyColor(config.getStringList("messages.self-vote-error")
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""));
    }

    public String getSuccessMessage() {
        return ColorUtil.applyColor(config.getStringList("messages.success")
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""));
    }

    public String getErrorMessage() {
        return ColorUtil.applyColor(config.getStringList("messages.error")
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""));
    }

    public String getUsageMessage() {
        return ColorUtil.applyColor(config.getStringList("messages.usage")
                .stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse("&cИспользуйте: /rep + <ник> или /rep - <ник>"));
    }

    // Формат суффикса, напр. "[%reputation%]"
    public String getReputationFormat() {
        return config.getString("messages.reputation-suffix", "[%reputation%]");
    }

    // Звук ошибки
    public String getErrorSound() {
        return config.getString("sounds.error", "ENTITY_VILLAGER_NO");
    }
}

// .