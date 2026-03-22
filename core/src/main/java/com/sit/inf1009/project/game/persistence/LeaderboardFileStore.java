package com.sit.inf1009.project.game.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFileStore {

    public List<LeaderboardRecord> load(String fileName) {
        List<LeaderboardRecord> records = new ArrayList<>();
        FileHandle file = Gdx.files.local(fileName);
        if (!file.exists()) {
            return records;
        }

        String content = file.readString("UTF-8");
        String[] lines = content.split("\\r?\\n");
        for (String rawLine : lines) {
            if (rawLine == null || rawLine.isBlank()) {
                continue;
            }

            String[] parts = rawLine.split("\\t", -1);
            if (parts.length < 4) {
                continue;
            }

            String name = parts[0].trim();
            int score;
            int presetIndex;
            try {
                score = Integer.parseInt(parts[1].trim());
                presetIndex = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            String uploadedPath = parts[3].trim();
            if (uploadedPath.isBlank()) {
                uploadedPath = null;
            }

            records.add(new LeaderboardRecord(name, score, presetIndex, uploadedPath));
        }
        return records;
    }

    public void save(String fileName, List<LeaderboardRecord> records) {
        StringBuilder sb = new StringBuilder();
        for (LeaderboardRecord record : records) {
            String safeName = sanitize(record.getName());
            String safePath = record.getUploadedPath() == null
                    ? ""
                    : record.getUploadedPath().replace('\t', ' ').replace('\n', ' ');
            sb.append(safeName)
                    .append('\t')
                    .append(record.getScore())
                    .append('\t')
                    .append(record.getPresetIndex())
                    .append('\t')
                    .append(safePath)
                    .append('\n');
        }

        Gdx.files.local(fileName).writeString(sb.toString(), false, "UTF-8");
    }

    private String sanitize(String input) {
        if (input == null) {
            return "Player";
        }
        String safe = input.replace('\t', ' ').replace('\n', ' ').trim();
        return safe.isBlank() ? "Player" : safe;
    }
}
