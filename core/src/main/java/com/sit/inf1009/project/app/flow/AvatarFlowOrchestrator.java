package com.sit.inf1009.project.app.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.game.ui.screens.AvatarSetupFlowScreen;

import java.io.File;

public final class AvatarFlowOrchestrator {

    public record AvatarSelectionState(
            Texture uploadedAvatarTexture,
            String uploadedAvatarPath,
            Texture selectedAvatarTexture,
            boolean selectedAvatarIsUploaded,
            int selectedPresetIndex
    ) {
    }

    private AvatarFlowOrchestrator() {
    }

    public static AvatarSelectionState defaultState(Texture[] presetAvatars) {
        int index = 0;
        Texture selected = (presetAvatars != null && presetAvatars.length > 0) ? presetAvatars[index] : null;
        return new AvatarSelectionState(null, null, selected, false, index);
    }

    public static AvatarSelectionState applyAvatarSelection(
            AvatarSetupFlowScreen.SelectionResult result,
            AvatarSelectionState current,
            Texture[] presetAvatars,
            int playerId,
            EntityManager entityManager) {
        if (result == null) {
            return current;
        }
        if (result.isUploaded()) {
            try {
                Texture newTexture = new Texture(Gdx.files.absolute(result.getUploadedPath()));
                if (current.uploadedAvatarTexture() != null) {
                    current.uploadedAvatarTexture().dispose();
                }
                AvatarSelectionState updated = new AvatarSelectionState(
                        newTexture,
                        result.getUploadedPath(),
                        newTexture,
                        true,
                        -1);
                applyAvatarToPlayer(updated.selectedAvatarTexture(), playerId, entityManager);
                return updated;
            } catch (Exception e) {
                return current;
            }
        }

        return selectPresetAvatar(result.getPresetIndex(), current, presetAvatars, playerId, entityManager);
    }

    public static AvatarSelectionState selectPresetAvatar(
            int index,
            AvatarSelectionState current,
            Texture[] presetAvatars,
            int playerId,
            EntityManager entityManager) {
        if (presetAvatars == null || index < 0 || index >= presetAvatars.length) {
            return current;
        }
        AvatarSelectionState updated = new AvatarSelectionState(
                current.uploadedAvatarTexture(),
                current.uploadedAvatarPath(),
                presetAvatars[index],
                false,
                index);
        applyAvatarToPlayer(updated.selectedAvatarTexture(), playerId, entityManager);
        return updated;
    }

    public static AvatarSelectionState applyUploadedImagePath(
            String path,
            AvatarSelectionState current,
            int playerId,
            EntityManager entityManager) {
        if (path == null || path.isBlank()) {
            return current;
        }
        try {
            Texture newTexture = new Texture(Gdx.files.absolute(path));
            if (current.uploadedAvatarTexture() != null) {
                current.uploadedAvatarTexture().dispose();
            }
            AvatarSelectionState updated = new AvatarSelectionState(
                    newTexture,
                    path,
                    newTexture,
                    true,
                    -1);
            applyAvatarToPlayer(updated.selectedAvatarTexture(), playerId, entityManager);
            return updated;
        } catch (Exception e) {
            return current;
        }
    }

    public static String uploadedFileName(String path) {
        return new File(path).getName();
    }

    public static void disposeUploadedTexture(AvatarSelectionState state) {
        if (state.uploadedAvatarTexture() != null) {
            state.uploadedAvatarTexture().dispose();
        }
    }

    private static void applyAvatarToPlayer(Texture selectedAvatarTexture, int playerId, EntityManager entityManager) {
        Entity player = getPlayerEntity(playerId, entityManager);
        if (player != null) {
            player.setTexture(selectedAvatarTexture);
        }
    }

    private static Entity getPlayerEntity(int playerId, EntityManager entityManager) {
        for (Entity entity : entityManager.getEntities()) {
            if (entity.getID() == playerId) {
                return entity;
            }
        }
        return null;
    }
}
