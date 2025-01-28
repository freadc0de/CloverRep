package com.fread.cloverrep;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RepTabCompleter implements TabCompleter {
    private final Main plugin;

    public RepTabCompleter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Подсказки для первого аргумента: "+" или "-"
        if (args.length == 1) {
            if ("+".startsWith(args[0])) suggestions.add("+");
            if ("-".startsWith(args[0])) suggestions.add("-");
        }

        // Подсказки для второго аргумента: имена онлайн-игроков
        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    suggestions.add(p.getName());
                }
            }
        }

        return suggestions;
    }
}

// .