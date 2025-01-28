package com.fread.cloverrep;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReputationPlaceholder extends PlaceholderExpansion {
    private final Main plugin;

    public ReputationPlaceholder(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cloverrep";
    }

    @Override
    public @NotNull String getAuthor() {
        return "fread";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("reputation")) {
            int reputation = plugin.getReputationManager().getReputation(player);
            return formatReputation(reputation);
        }

        return null;
    }

    private String formatReputation(int reputation) {
        ConfigManager config = plugin.getConfigManager();
        String color;

        if (reputation > 0) {
            color = config.getPositiveReputationColor();
        } else if (reputation < 0) {
            color = config.getNegativeReputationColor();
        } else {
            color = config.getNeutralReputationColor();
        }

        return ColorUtil.applyColor(color + reputation);
    }
}

// .
