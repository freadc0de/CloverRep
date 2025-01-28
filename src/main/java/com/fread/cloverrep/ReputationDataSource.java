package com.fread.cloverrep;

import java.util.Set;

public interface ReputationDataSource {

    int getReputation(String playerName);

    void setReputation(String playerName, int reputation);

    boolean hasVoted(String voter, String target);

    void addVoter(String voter, String target);

    Set<String> getVoters(String playerName);

    void saveData();
}

// .