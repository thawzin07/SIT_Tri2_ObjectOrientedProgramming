package com.sit.inf1009.project.app.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.app.GameState;
import com.sit.inf1009.project.app.ui.AppUiRenderer;
import com.sit.inf1009.project.game.persistence.LeaderboardStore;
import com.sit.inf1009.project.game.persistence.LeaderboardRecord;
import com.sit.inf1009.project.game.ui.LeaderboardNameEditor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class LeaderboardFlowOrchestrator {

    public record NameEditState(String playerNameInput,
                                boolean leaderboardNameEditing,
                                boolean confirmed,
                                boolean canceled) {
    }

    public record SubmitResult(
            List<LeaderboardEntry> entries,
            boolean submitted,
            String failureReason) {
    }

    public static final class LeaderboardEntry implements AppUiRenderer.LeaderboardRow {
        private final String name;
        private final int score;
        private final Texture avatarTexture;
        private final boolean ownsTexture;
        private final int presetIndex;
        private final String uploadedPath;

        public LeaderboardEntry(String name,
                                int score,
                                Texture avatarTexture,
                                boolean ownsTexture,
                                int presetIndex,
                                String uploadedPath) {
            this.name = name;
            this.score = score;
            this.avatarTexture = avatarTexture;
            this.ownsTexture = ownsTexture;
            this.presetIndex = presetIndex;
            this.uploadedPath = uploadedPath;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public Texture getAvatarTexture() {
            return avatarTexture;
        }

        public boolean ownsTexture() {
            return ownsTexture;
        }

        public int presetIndex() {
            return presetIndex;
        }

        public String uploadedPath() {
            return uploadedPath;
        }
    }

    private LeaderboardFlowOrchestrator() {
    }

    public static NameEditState updateNameTyping(GameState gameState,
                                                 boolean leaderboardNameEditing,
                                                 String playerNameInput) {
        if (gameState != GameState.LEADERBOARD_ENTRY || !leaderboardNameEditing) {
            return new NameEditState(playerNameInput, leaderboardNameEditing, false, false);
        }

        LeaderboardNameEditor.Result result = LeaderboardNameEditor.update(playerNameInput, 24);
        if (result.isConfirmed() || result.isCanceled()) {
            return new NameEditState(result.getUpdatedName(), false, result.isConfirmed(), result.isCanceled());
        }
        return new NameEditState(result.getUpdatedName(), true, false, false);
    }

    public static SubmitResult submitEntry(List<LeaderboardEntry> existingEntries,
                                           String playerNameInput,
                                           int score,
                                           Texture selectedAvatarTexture,
                                           boolean selectedAvatarIsUploaded,
                                           int selectedPresetIndex,
                                           String uploadedAvatarPath) {
        if (playerNameInput == null || playerNameInput.isBlank()) {
            return new SubmitResult(existingEntries, false, "Please enter your name");
        }
        if (selectedAvatarTexture == null) {
            return new SubmitResult(existingEntries, false, "Please choose/upload an avatar image");
        }

        Texture entryTexture = selectedAvatarTexture;
        boolean ownsTexture = false;
        int entryPresetIndex = selectedAvatarIsUploaded ? -1 : selectedPresetIndex;
        String entryUploadedPath = selectedAvatarIsUploaded ? uploadedAvatarPath : null;

        if (selectedAvatarIsUploaded) {
            if (uploadedAvatarPath == null || uploadedAvatarPath.isBlank()) {
                return new SubmitResult(existingEntries, false, "Uploaded avatar path missing");
            }
            try {
                entryTexture = new Texture(Gdx.files.absolute(uploadedAvatarPath));
                ownsTexture = true;
            } catch (Exception e) {
                return new SubmitResult(existingEntries, false, "Failed to store uploaded avatar for leaderboard");
            }
        }

        List<LeaderboardEntry> next = new ArrayList<>(existingEntries);
        next.add(new LeaderboardEntry(
                sanitizeName(playerNameInput),
                score,
                entryTexture,
                ownsTexture,
                entryPresetIndex,
                entryUploadedPath));
        next.sort(Comparator.comparingInt(LeaderboardEntry::getScore).reversed());
        return new SubmitResult(next, true, null);
    }

    public static List<LeaderboardEntry> loadEntries(LeaderboardStore leaderboardStore,
                                                     String fileName,
                                                     Texture[] presetAvatars) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        List<LeaderboardRecord> records = leaderboardStore.load(fileName);
        for (LeaderboardRecord record : records) {
            String name = record.getName();
            int score = record.getScore();
            int presetIndex = record.getPresetIndex();
            String uploadedPath = record.getUploadedPath();

            Texture texture = null;
            boolean ownsTexture = false;
            if (uploadedPath != null) {
                try {
                    texture = new Texture(Gdx.files.absolute(uploadedPath));
                    ownsTexture = true;
                } catch (Exception ignored) {
                    texture = null;
                    ownsTexture = false;
                }
            }

            if (texture == null && presetAvatars != null && presetIndex >= 0 && presetIndex < presetAvatars.length) {
                texture = presetAvatars[presetIndex];
            }

            entries.add(new LeaderboardEntry(name, score, texture, ownsTexture, presetIndex, uploadedPath));
        }

        entries.sort(Comparator.comparingInt(LeaderboardEntry::getScore).reversed());
        return entries;
    }

    public static void saveEntries(LeaderboardStore leaderboardStore,
                                   String fileName,
                                   List<LeaderboardEntry> entries) {
        List<LeaderboardRecord> records = new ArrayList<>();
        for (LeaderboardEntry entry : entries) {
            records.add(new LeaderboardRecord(
                    sanitizeName(entry.getName()),
                    entry.getScore(),
                    entry.presetIndex(),
                    entry.uploadedPath()));
        }
        leaderboardStore.save(fileName, records);
    }

    private static String sanitizeName(String input) {
        if (input == null) {
            return "Player";
        }
        String safe = input.replace('\t', ' ').replace('\n', ' ').trim();
        return safe.isBlank() ? "Player" : safe;
    }
}
