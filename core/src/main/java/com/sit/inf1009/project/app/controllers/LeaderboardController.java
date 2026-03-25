package com.sit.inf1009.project.app.controllers;

import com.sit.inf1009.project.game.persistence.LeaderboardRecord;
import com.sit.inf1009.project.game.persistence.LeaderboardStore;

import java.util.ArrayList;
import java.util.List;

public final class LeaderboardController {
    private final LeaderboardStore fileStore;

    public LeaderboardController(LeaderboardStore fileStore) {
        this.fileStore = fileStore;
    }

    public List<LeaderboardRecord> load(String fileName) {
        return new ArrayList<>(fileStore.load(fileName));
    }

    public void save(String fileName, List<LeaderboardRecord> records) {
        fileStore.save(fileName, records);
    }

    public String sanitizeName(String input) {
        if (input == null) {
            return "Player";
        }
        String safe = input.replace('\t', ' ').replace('\n', ' ').trim();
        return safe.isBlank() ? "Player" : safe;
    }
}
