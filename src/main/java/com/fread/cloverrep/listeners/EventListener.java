package com.fread.cloverrep.listeners;

import com.fread.cloverrep.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    private final Main plugin;

    public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getReputationManager().updatePlayerNameColor(player);
        plugin.getReputationManager().updatePlayerSuffix(player);
    }
}

// .