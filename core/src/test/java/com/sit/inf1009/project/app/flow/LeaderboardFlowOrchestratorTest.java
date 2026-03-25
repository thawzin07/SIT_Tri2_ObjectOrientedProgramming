package com.sit.inf1009.project.app.flow;

import com.sit.inf1009.project.app.GameState;
import com.sit.inf1009.project.game.persistence.LeaderboardRecord;
import com.sit.inf1009.project.game.persistence.LeaderboardStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderboardFlowOrchestratorTest {

    @Test
    void updateNameTypingReturnsInputWhenNotInLeaderboardEntryState() {
        LeaderboardFlowOrchestrator.NameEditState state =
                LeaderboardFlowOrchestrator.updateNameTyping(GameState.FOOD_MENU, true, "Alice");

        assertEquals("Alice", state.playerNameInput());
        assertTrue(state.leaderboardNameEditing());
        assertFalse(state.confirmed());
        assertFalse(state.canceled());
    }

    @Test
    void submitEntryRejectsBlankName() {
        LeaderboardFlowOrchestrator.SubmitResult result = LeaderboardFlowOrchestrator.submitEntry(
                List.of(),
                "   ",
                12,
                null,
                false,
                0,
                null);

        assertFalse(result.submitted());
        assertEquals("Please enter your name", result.failureReason());
    }

    @Test
    void submitEntryRejectsMissingAvatarTexture() {
        LeaderboardFlowOrchestrator.SubmitResult result = LeaderboardFlowOrchestrator.submitEntry(
                List.of(),
                "Alice",
                12,
                null,
                false,
                0,
                null);

        assertFalse(result.submitted());
        assertEquals("Please choose/upload an avatar image", result.failureReason());
    }

    @Test
    void loadEntriesSortsByScoreDescendingWithoutTextureDependencies() {
        InMemoryStore store = new InMemoryStore();
        store.data = List.of(
                new LeaderboardRecord("low", 3, -1, null),
                new LeaderboardRecord("high", 50, -1, null),
                new LeaderboardRecord("mid", 10, -1, null)
        );

        List<LeaderboardFlowOrchestrator.LeaderboardEntry> entries =
                LeaderboardFlowOrchestrator.loadEntries(store, "leaderboard.txt", null);

        assertEquals(3, entries.size());
        assertEquals("high", entries.get(0).getName());
        assertEquals("mid", entries.get(1).getName());
        assertEquals("low", entries.get(2).getName());
    }

    @Test
    void saveEntriesMapsFieldsToStoreRecords() {
        InMemoryStore store = new InMemoryStore();
        List<LeaderboardFlowOrchestrator.LeaderboardEntry> entries = List.of(
                new LeaderboardFlowOrchestrator.LeaderboardEntry("A\tName", 7, null, false, 2, null),
                new LeaderboardFlowOrchestrator.LeaderboardEntry("B", 5, null, false, -1, "C:\\tmp\\avatar.png")
        );

        LeaderboardFlowOrchestrator.saveEntries(store, "leaderboard.txt", entries);

        assertNotNull(store.lastSavedFile);
        assertEquals("leaderboard.txt", store.lastSavedFile);
        assertEquals(2, store.data.size());
        assertEquals("A Name", store.data.get(0).getName());
        assertEquals(7, store.data.get(0).getScore());
        assertEquals(2, store.data.get(0).getPresetIndex());
        assertEquals(null, store.data.get(0).getUploadedPath());
    }

    private static class InMemoryStore implements LeaderboardStore {
        List<LeaderboardRecord> data = new ArrayList<>();
        String lastSavedFile;

        @Override
        public List<LeaderboardRecord> load(String fileName) {
            return data;
        }

        @Override
        public void save(String fileName, List<LeaderboardRecord> records) {
            lastSavedFile = fileName;
            data = new ArrayList<>(records);
        }
    }
}
