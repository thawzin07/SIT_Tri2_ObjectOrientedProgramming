package com.sit.inf1009.project.game.persistence;

import java.util.List;

public interface LeaderboardStore {
    List<LeaderboardRecord> load(String fileName);

    void save(String fileName, List<LeaderboardRecord> records);
}
