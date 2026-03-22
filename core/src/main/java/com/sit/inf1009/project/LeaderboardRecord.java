package com.sit.inf1009.project;

public class LeaderboardRecord {
    private final String name;
    private final int score;
    private final int presetIndex;
    private final String uploadedPath;

    public LeaderboardRecord(String name, int score, int presetIndex, String uploadedPath) {
        this.name = name;
        this.score = score;
        this.presetIndex = presetIndex;
        this.uploadedPath = uploadedPath;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getPresetIndex() {
        return presetIndex;
    }

    public String getUploadedPath() {
        return uploadedPath;
    }
}
