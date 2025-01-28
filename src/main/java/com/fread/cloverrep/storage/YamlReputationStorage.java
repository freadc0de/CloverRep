package com.fread.cloverrep.storage;

import com.fread.cloverrep.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YamlReputationStorage implements ReputationDataSource {

    private final File file;
    private final FileConfiguration config;

    public YamlReputationStorage(Main plugin) {
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

    @Override
    public int getReputation(String playerName) {
        return config.getInt("players." + playerName + ".reputation", 0);
    }

    @Override
    public void setReputation(String playerName, int reputation) {
        config.set("players." + playerName + ".reputation", reputation);
        saveData();
    }

    @Override
    public boolean hasVoted(String voter, String target) {
        return getVoters(voter).contains(target);
    }

    @Override
    public void addVoter(String voter, String target) {
        Set<String> voters = getVoters(voter);
        voters.add(target);
        config.set("players." + voter + ".voters", List.copyOf(voters));
        saveData();
    }

    @Override
    public Set<String> getVoters(String playerName) {
        List<String> votersList = config.getStringList("players." + playerName + ".voters");
        return new HashSet<>(votersList);
    }

    @Override
    public void saveData() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// .