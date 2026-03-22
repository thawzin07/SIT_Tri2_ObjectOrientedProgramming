package com.sit.inf1009.project.app.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class AvatarFlowOrchestratorTest {

    @Test
    void defaultStateUsesFirstPresetWhenAvailable() {
        AvatarFlowOrchestrator.AvatarSelectionState state =
                AvatarFlowOrchestrator.defaultState(new com.badlogic.gdx.graphics.Texture[1]);

        assertEquals(0, state.selectedPresetIndex());
        assertFalse(state.selectedAvatarIsUploaded());
    }

    @Test
    void defaultStateHandlesEmptyPresetArray() {
        AvatarFlowOrchestrator.AvatarSelectionState state =
                AvatarFlowOrchestrator.defaultState(new com.badlogic.gdx.graphics.Texture[0]);

        assertEquals(0, state.selectedPresetIndex());
        assertFalse(state.selectedAvatarIsUploaded());
        assertNull(state.selectedAvatarTexture());
    }

    @Test
    void uploadedFileNameReturnsLeafName() {
        String fileName = AvatarFlowOrchestrator.uploadedFileName("C:\\tmp\\avatars\\player.png");
        assertEquals("player.png", fileName);
    }
}
