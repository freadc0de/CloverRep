package com.fread.cloverrep;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigManager configManager;
    private ReputationManager reputationManager;

    @Override
    public void onEnable() {
        instance = this;

        // Инициализация менеджеров
        configManager = new ConfigManager(this);
        reputationManager = new ReputationManager(this); // Сам выберет MySQL или YAML

        // Регистрация команды /rep и её таб-комплитера
        if (getCommand("rep") != null) {
            getCommand("rep").setExecutor(new Commands(this));
            getCommand("rep").setTabCompleter(new RepTabCompleter(this));
        }

        // Регистрация событий
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        // Проверяем наличие PlaceholderAPI
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ReputationPlaceholder(this).register();
            getLogger().info("PlaceholderAPI обнаружен. Плейсхолдеры зарегистрированы!");
        }

        getLogger().info("CloverRep успешно запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Сохранение данных репутации...");
        reputationManager.saveAllData();
        getLogger().info("CloverRep был отключен.");
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ReputationManager getReputationManager() {
        return reputationManager;
    }
}
