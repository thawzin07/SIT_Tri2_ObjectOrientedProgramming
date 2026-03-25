package com.sit.inf1009.project.app.controllers;

import com.sit.inf1009.project.app.DifficultyPreset;
import com.sit.inf1009.project.game.domain.DifficultyConfig;

public final class SettingsController {
    private DifficultyPreset preset;
    private DifficultyConfig config;

    public SettingsController(DifficultyPreset initialPreset) {
        this.preset = initialPreset;
        this.config = toConfig(initialPreset);
    }

    public void setPreset(DifficultyPreset preset) {
        if (preset == null) {
            return;
        }
        this.preset = preset;
        this.config = toConfig(preset);
    }

    public DifficultyPreset getPreset() {
        return preset;
    }

    public DifficultyConfig getConfig() {
        return config;
    }

    private DifficultyConfig toConfig(DifficultyPreset preset) {
        return new DifficultyConfig(
                preset.getStartingTimer(),
                preset.getNpcCount(),
                preset.getNpcSpeed(),
                preset.getHealthyScoreBonus(),
                preset.getHealthyTimerBonus(),
                preset.getUnhealthyTimerPenalty(),
                preset.getFoodEntityCount());
    }
}
