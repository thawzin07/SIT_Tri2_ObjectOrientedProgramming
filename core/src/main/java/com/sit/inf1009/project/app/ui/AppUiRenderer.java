package com.sit.inf1009.project.app.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.sit.inf1009.project.app.DifficultyPreset;
import com.sit.inf1009.project.app.controllers.GameFlowController;
import com.sit.inf1009.project.game.ui.UiPanelRenderer;

import java.util.List;

public final class AppUiRenderer {

    public interface LeaderboardRow {
        String getName();
        int getScore();
        Texture getAvatarTexture();
    }

    public enum DifficultyAction {
        NONE,
        SET_EASY,
        SET_NORMAL,
        SET_HARD,
        BACK_TO_MENU
    }

    public enum HowToPlayAction {
        NONE,
        BACK_TO_MENU,
        BACK_TO_PAUSE
    }

    public enum LeaderboardEntryAction {
        NONE,
        ENABLE_NAME_EDIT,
        REQUEST_UPLOAD,
        SUBMIT,
        BACK_TO_MENU
    }

    public enum LeaderboardViewAction {
        NONE,
        FOOTER_CLICKED
    }

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final GameFlowController flowController;

    private boolean clickPending;
    private float clickX;
    private float clickY;

    public AppUiRenderer(ShapeRenderer shapeRenderer,
                         SpriteBatch batch,
                         BitmapFont font,
                         OrthographicCamera camera,
                         GameFlowController flowController) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.camera = camera;
        this.flowController = flowController;
    }

    public void captureClick(Vector3 touchPos) {
        clickPending = Gdx.input.justTouched();
        if (!clickPending) {
            return;
        }
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        clickX = touchPos.x;
        clickY = touchPos.y;
    }

    public boolean consumeClick(Rectangle bounds) {
        if (!clickPending || !bounds.contains(clickX, clickY)) {
            return false;
        }
        clickPending = false;
        return true;
    }

    public void applyFullScreenProjection() {
        int logicalWidth = Gdx.graphics.getWidth();
        int logicalHeight = Gdx.graphics.getHeight();
        int pixelWidth = Gdx.graphics.getBackBufferWidth();
        int pixelHeight = Gdx.graphics.getBackBufferHeight();
        Gdx.gl.glViewport(0, 0, pixelWidth, pixelHeight);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, logicalWidth, logicalHeight);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
    }

    public void drawScreenPanel(Rectangle panel) {
        UiPanelRenderer.drawScreenPanel(shapeRenderer, panel);
    }

    public void drawActionButton(Rectangle bounds, Color fillColor) {
        UiPanelRenderer.drawActionButton(shapeRenderer, bounds, fillColor);
    }

    public void drawTextInputField(Rectangle bounds, boolean active) {
        UiPanelRenderer.drawTextInputField(shapeRenderer, bounds, active);
    }

    public void drawStatus(float x, float y) {
        if (!flowController.hasStatus()) {
            return;
        }
        font.draw(batch, flowController.getStatusMessage(), x, y);
    }

    public DifficultyAction renderDifficultySettings(DifficultyPreset difficultyPreset) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(760f, width - 80f);
        float panelH = Math.min(500f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        Rectangle easyButton = new Rectangle(panel.x + 40f, panel.y + panelH - 180f, panelW - 80f, 48f);
        Rectangle normalButton = new Rectangle(panel.x + 40f, panel.y + panelH - 240f, panelW - 80f, 48f);
        Rectangle hardButton = new Rectangle(panel.x + 40f, panel.y + panelH - 300f, panelW - 80f, 48f);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        DifficultyAction action = DifficultyAction.NONE;
        if (consumeClick(easyButton)) action = DifficultyAction.SET_EASY;
        if (consumeClick(normalButton)) action = DifficultyAction.SET_NORMAL;
        if (consumeClick(hardButton)) action = DifficultyAction.SET_HARD;
        if (consumeClick(backButton)) action = DifficultyAction.BACK_TO_MENU;

        drawScreenPanel(panel);
        drawActionButton(easyButton, difficultyPreset == DifficultyPreset.EASY ? new Color(0.16f, 0.62f, 0.2f, 1f) : new Color(0.12f, 0.34f, 0.18f, 1f));
        drawActionButton(normalButton, difficultyPreset == DifficultyPreset.NORMAL ? new Color(0.1f, 0.45f, 0.78f, 1f) : new Color(0.1f, 0.26f, 0.45f, 1f));
        drawActionButton(hardButton, difficultyPreset == DifficultyPreset.HARD ? new Color(0.75f, 0.22f, 0.22f, 1f) : new Color(0.4f, 0.16f, 0.16f, 1f));
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        font.draw(batch, "GAME SETTINGS", panel.x + 40f, panel.y + panelH - 28f);
        font.draw(batch, "Difficulty: " + difficultyPreset.getLabel(), panel.x + 40f, panel.y + panelH - 58f);
        font.draw(batch, "Easy   - 75s, slower, +6s / -3s submit, 10 Food items", easyButton.x + 20f, easyButton.y + 30f);
        font.draw(batch, "Normal - 60s, balanced, +5s / -5s submit, 15 Food items", normalButton.x + 20f, normalButton.y + 30f);
        font.draw(batch, "Hard   - 45s, faster, +4s / -6s submit, 20 Food items", hardButton.x + 20f, hardButton.y + 30f);
        font.draw(batch, "Back to Main Menu", backButton.x + 20f, backButton.y + 28f);
        drawStatus(20f, 24f);
        batch.end();
        return action;
    }

    public HowToPlayAction renderHowToPlay(boolean rulesOpenedFromPause) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(840f, width - 80f);
        float panelH = Math.min(520f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        HowToPlayAction action = HowToPlayAction.NONE;
        if (consumeClick(backButton)) {
            action = rulesOpenedFromPause ? HowToPlayAction.BACK_TO_PAUSE : HowToPlayAction.BACK_TO_MENU;
        }

        drawScreenPanel(panel);
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float textX = panel.x + 40f;
        float topY = panel.y + panelH - 28f;
        font.draw(batch, "HOW TO PLAY", textX, topY);
        font.draw(batch, "1. Press START on the main menu.", textX, topY - 42f);
        font.draw(batch, "2. Select a preset avatar or upload your own image.", textX, topY - 70f);
        font.draw(batch, "3. Press Start Game to begin.", textX, topY - 98f);
        font.draw(batch, "4. Move with WASD and catch food items.", textX, topY - 126f);
        font.draw(batch, "5. Build a healthy plate target: Veg: 2-4 Protein: 1-3 Carbs: 1-2 Oil: 0-1", textX, topY - 154f);
        font.draw(batch, "6. Press Enter to submit plate (this resets plate).", textX, topY - 182f);
        font.draw(batch, "7. Press R to clear plate, Space to pause/resume.", textX, topY - 210f);
        font.draw(batch, "8. Timer end -> submit name/avatar to leaderboard.", textX, topY - 238f);
        String backText = rulesOpenedFromPause ? "Back to Pause Menu" : "Back to Main Menu";
        font.draw(batch, backText, backButton.x + 20f, backButton.y + 28f);
        drawStatus(20f, 24f);
        batch.end();
        return action;
    }

    public LeaderboardEntryAction renderLeaderboardEntry(int finalScore,
                                                         Texture selectedAvatarTexture,
                                                         String playerNameInput,
                                                         boolean leaderboardNameEditing) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(760f, width - 80f);
        float panelH = Math.min(520f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        Rectangle nameField = new Rectangle(panel.x + 40f, panel.y + panelH - 170f, panelW - 80f, 48f);
        Rectangle uploadButton = new Rectangle(panel.x + 40f, panel.y + panelH - 235f, panelW - 80f, 48f);
        Rectangle submitButton = new Rectangle(panel.x + 40f, panel.y + panelH - 300f, panelW - 80f, 48f);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        LeaderboardEntryAction action = LeaderboardEntryAction.NONE;
        if (consumeClick(nameField)) action = LeaderboardEntryAction.ENABLE_NAME_EDIT;
        if (consumeClick(uploadButton)) action = LeaderboardEntryAction.REQUEST_UPLOAD;
        if (consumeClick(submitButton)) action = LeaderboardEntryAction.SUBMIT;
        if (consumeClick(backButton)) action = LeaderboardEntryAction.BACK_TO_MENU;

        drawScreenPanel(panel);
        drawTextInputField(nameField, leaderboardNameEditing);
        drawActionButton(uploadButton, new Color(0.12f, 0.34f, 0.5f, 1f));
        drawActionButton(submitButton, new Color(0.13f, 0.47f, 0.2f, 1f));
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float headerY = panel.y + panelH - 28f;
        font.draw(batch, "RUN COMPLETE", panel.x + 40f, headerY);
        font.draw(batch, "Final Score: " + finalScore, panel.x + 40f, headerY - 30f);
        font.draw(batch, "Enter your name and submit to leaderboard", panel.x + 40f, headerY - 58f);

        if (selectedAvatarTexture != null) {
            font.draw(batch, "Avatar:", panel.x + 40f, headerY - 86f);
            batch.draw(selectedAvatarTexture, panel.x + 90f, headerY - 104f, 28f, 28f);
        } else {
            font.draw(batch, "Avatar: <none>", panel.x + 40f, headerY - 86f);
        }

        String shownName = playerNameInput.isBlank() ? "Type your name..." : playerNameInput;
        if (leaderboardNameEditing && ((System.currentTimeMillis() / 350L) % 2L == 0L)) {
            shownName += "_";
        }
        font.draw(batch, shownName, nameField.x + 16f, nameField.y + 30f);
        font.draw(batch, "Upload / Change Image", uploadButton.x + 16f, uploadButton.y + 30f);
        font.draw(batch, "Submit to Leaderboard", submitButton.x + 16f, submitButton.y + 30f);
        font.draw(batch, "Back to Main Menu", backButton.x + 16f, backButton.y + 28f);
        drawStatus(20f, 24f);
        batch.end();
        return action;
    }

    public LeaderboardViewAction renderLeaderboardView(List<? extends LeaderboardRow> rows,
                                                       boolean leaderboardOpenedFromMenu) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(800f, width - 80f);
        float panelH = Math.min(540f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        String footerLabel = leaderboardOpenedFromMenu ? "Back to Main Menu" : "Play Again";
        Rectangle footerButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        LeaderboardViewAction action = consumeClick(footerButton)
                ? LeaderboardViewAction.FOOTER_CLICKED
                : LeaderboardViewAction.NONE;

        drawScreenPanel(panel);
        drawActionButton(footerButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float topY = panel.y + panelH - 28f;
        font.draw(batch, "LEADERBOARD", panel.x + 40f, topY);

        int maxRows = Math.min(10, rows.size());
        float rowY = topY - 42f;
        for (int i = 0; i < maxRows; i++) {
            LeaderboardRow entry = rows.get(i);
            float rowX = panel.x + 40f;
            font.draw(batch, String.format("%2d.", i + 1), rowX, rowY);
            if (entry.getAvatarTexture() != null) {
                batch.draw(entry.getAvatarTexture(), rowX + 34f, rowY - 18f, 24f, 24f);
            }
            font.draw(batch, entry.getName(), rowX + 70f, rowY);
            font.draw(batch, "Score: " + entry.getScore(), panel.x + panelW - 170f, rowY);
            rowY -= 30f;
        }

        if (rows.isEmpty()) {
            font.draw(batch, "No entries yet.", panel.x + 40f, topY - 42f);
        }

        font.draw(batch, footerLabel, footerButton.x + 16f, footerButton.y + 28f);
        drawStatus(20f, 24f);
        batch.end();
        return action;
    }
}
