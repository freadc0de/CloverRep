package com.fread.cloverrep;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReputationStorage {
    private final File file;
    private final FileConfiguration config;

    public ReputationStorage(Main plugin) {
        this.file = new File(plugin.getDataFolder(), "reputation.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл reputation.yml");
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public int getReputation(String playerName) {
        return config.getInt("players." + playerName + ".reputation", 0);
    }

    public void setReputation(String playerName, int reputation) {
        config.set("players." + playerName + ".reputation", reputation);
        saveData();
    }

    public Set<String> getVoters(String voter) {
        List<String> votersList = config.getStringList("players." + voter + ".voters");
        return new HashSet<>(votersList);
    }

    public void addVoter(String voter, String target) {
        Set<String> voters = getVoters(voter);
        voters.add(target);
        config.set("players." + voter + ".voters", List.copyOf(voters));
        saveData();
    }

    public boolean hasVoted(String voter, String target) {
        return getVoters(voter).contains(target);
    }

    public void saveData() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
