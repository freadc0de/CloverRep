package com.fread.cloverrep;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.applyColor("&cЭта команда доступна только игрокам."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getUsageMessage());
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorUtil.applyColor(plugin.getConfigManager().getErrorMessage()));
            return true;
        }

        // Игрок не может голосовать сам за себя
        if (player.equals(target)) {
            player.sendMessage(ColorUtil.applyColor(plugin.getConfigManager().getSelfVoteErrorMessage()));
            SoundUtil.playErrorSound(player, plugin.getConfigManager().getErrorSound());
            return true;
        }

        // Проверяем, может ли игрок голосовать
        if (!plugin.getReputationManager().canVote(player, target)) {
            SoundUtil.playErrorSound(player, plugin.getConfigManager().getErrorSound());
            player.sendMessage(ColorUtil.applyColor(plugin.getConfigManager().getAlreadyVotedMessage()));
            return true;
        }

        int reputationChange = args[0].equalsIgnoreCase("+") ? 1 : -1;
        plugin.getReputationManager().addReputation(player, target, reputationChange);

        player.sendMessage(ColorUtil.applyColor(plugin.getConfigManager().getSuccessMessage()));
        return true;
    }
}

// .