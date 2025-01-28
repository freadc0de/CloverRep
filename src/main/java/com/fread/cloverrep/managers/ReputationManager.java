package com.fread.cloverrep.managers;

import com.fread.cloverrep.*;
import com.fread.cloverrep.storage.MySQLReputationStorage;
import com.fread.cloverrep.storage.ReputationDataSource;
import com.fread.cloverrep.storage.YamlReputationStorage;
import com.fread.cloverrep.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ReputationManager {
    private final ReputationDataSource storage;
    private final Scoreboard scoreboard;

    public ReputationManager(Main plugin) {
        String storageType = plugin.getConfigManager().getStorageType();

        if ("mysql".equals(storageType)) {
            storage = new MySQLReputationStorage(plugin);
            plugin.getLogger().info("Используется хранилище MySQL");
        } else {
            storage = new YamlReputationStorage(plugin);
            plugin.getLogger().info("Используется локальное YAML-хранилище");
        }

        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public int getReputation(Player player) {
        return storage.getReputation(player.getName());
    }

    public boolean canVote(Player voter, Player target) {
        return !storage.hasVoted(voter.getName(), target.getName());
    }

    public void addReputation(Player voter, Player target, int amount) {
        // Проверяем, голосовал ли уже
        if (!canVote(voter, target)) {
            voter.sendMessage("Вы уже голосовали за этого игрока!");
            return;
        }

        // Записываем факт голосования
        storage.addVoter(voter.getName(), target.getName());

        // Ставим/меняем репутацию
        int currentReputation = storage.getReputation(target.getName());
        int newReputation = currentReputation + amount;
        storage.setReputation(target.getName(), newReputation);

        // Обновляем отображение (ник, суффикс)
        updatePlayerNameColor(target);
        updatePlayerSuffix(target);
    }

    public void saveAllData() {
        // Если YamlReputationStorage — файлы сохранятся
        // Если MySQLReputationStorage — пустой метод
        storage.saveData();
    }

    public void updatePlayerNameColor(Player player) {
        int reputation = getReputation(player);

        removePlayerFromAllTeams(player);

        // Пример: если репутация <= -20, даём игроку "тёмный" ник
        if (reputation <= -20) {
            Team darkTeam = scoreboard.getTeam("dark_nick");
            if (darkTeam == null) {
                darkTeam = scoreboard.registerNewTeam("dark_nick");
                darkTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }
            darkTeam.addEntry(player.getName());
        }
    }

    public void updatePlayerSuffix(Player player) {
        int reputation = getReputation(player);

        // Выбираем цвет в зависимости от репутации
        ConfigManager config = Main.getInstance().getConfigManager();
        String color;
        if (reputation > 0) {
            color = config.getPositiveReputationColor();
        } else if (reputation < 0) {
            color = config.getNegativeReputationColor();
        } else {
            color = config.getNeutralReputationColor();
        }

        // Подставляем значение репутации в формат
        String suffix = config.getReputationFormat().replace("%reputation%", String.valueOf(reputation));
        suffix = ColorUtil.applyColor(color + suffix);

        // Создаём команду (Team) с именем игрока, чтобы задать суффикс
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.setSuffix(suffix);
        team.addEntry(player.getName());
    }

    private void removePlayerFromAllTeams(Player player) {
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }
    }
}

// .