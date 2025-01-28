package com.fread.cloverrep;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playErrorSound(Player player, String soundName) {
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        } catch (IllegalArgumentException e) {
            Main.getInstance().getLogger().warning("Некорректное имя звука: " + soundName);
        }
    }
}
