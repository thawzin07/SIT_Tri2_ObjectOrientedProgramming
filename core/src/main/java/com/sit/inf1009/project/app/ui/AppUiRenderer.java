package com.sit.inf1009.project.app.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
        BACK_TO_PAUSE, 
        CONTINUE_TO_AVATAR
    }

    public enum LeaderboardEntryAction {
        NONE, ENABLE_NAME_EDIT, REQUEST_UPLOAD, SUBMIT, BACK_TO_MENU
    }

    public enum LeaderboardViewAction {
        NONE, FOOTER_CLICKED
    }

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final GameFlowController flowController;
    
    private final Texture easyDifficultyIcon;
    private final Texture normalDifficultyIcon;
    private final Texture hardDifficultyIcon;
    private final Texture timerIcon;
    private final Texture playingBackgroundTexture;
    
    private final Texture vegeIcon;
    private final Texture proteinIcon;
    private final Texture carbIcon;
    private final Texture oilIcon;

    private boolean clickPending;
    private float clickX;
    private float clickY;

    public AppUiRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, OrthographicCamera camera, GameFlowController flowController) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.camera = camera;
        this.flowController = flowController;
        this.easyDifficultyIcon = loadTextureOrNull("easy.png");
        this.normalDifficultyIcon = loadTextureOrNull("normal.png");
        this.hardDifficultyIcon = loadTextureOrNull("hard.png");
        this.timerIcon = loadTextureOrNull("timer.png");
        this.playingBackgroundTexture = loadTextureOrNull("playingbackground.jpg");
        this.vegeIcon = loadTextureOrNull("vege.png");
        this.proteinIcon = loadTextureOrNull("protein.png");
        this.carbIcon = loadTextureOrNull("carb.png");
        this.oilIcon = loadTextureOrNull("oil.png");
    }

    public void captureClick(Vector3 touchPos) {
        clickPending = Gdx.input.justTouched();
        if (!clickPending) return;
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        clickX = touchPos.x;
        clickY = touchPos.y;
    }

    public boolean consumeClick(Rectangle bounds) {
        if (!clickPending || !bounds.contains(clickX, clickY)) return false;
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
        if (!flowController.hasStatus()) return;

        String statusMessage = flowController.getStatusMessage();
        GlyphLayout layout = new GlyphLayout(font, statusMessage);
        float padding = 8f;

        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
        shapeRenderer.rect(x - padding, y - layout.height - padding, layout.width + (padding * 2), layout.height + (padding * 2));
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        font.draw(batch, statusMessage, x, y);
    }

    public Texture getTimerIcon() { return timerIcon; }
    public Texture getPlayingBackgroundTexture() { return playingBackgroundTexture; }

    public Texture getDifficultyIcon(DifficultyPreset difficultyPreset) {
        if (difficultyPreset == null) return null;
        return switch (difficultyPreset) {
            case EASY -> easyDifficultyIcon;
            case NORMAL -> normalDifficultyIcon;
            case HARD -> hardDifficultyIcon;
        };
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
        drawDifficultyOption(batch, easyDifficultyIcon, easyButton, "Easy   - 75s, +6s / -3s submit");
        drawDifficultyOption(batch, normalDifficultyIcon, normalButton, "Normal - 60s, +5s / -5s submit");
        drawDifficultyOption(batch, hardDifficultyIcon, hardButton, "Hard   - 45s, +4s / -6s submit");
        font.draw(batch, "Back to Main Menu", backButton.x + 20f, backButton.y + 28f);
        drawStatus(20f, 24f);
        batch.end();
        return action;
    }

    public HowToPlayAction renderHowToPlay(boolean rulesOpenedFromPause, boolean rulesOpenedFromStart) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(840f, width - 80f);
        float panelH = Math.min(620f, height - 40f); 
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 24f, panelW - 80f, 44f);
        Rectangle continueButton = null;
        if (rulesOpenedFromStart) {
            continueButton = new Rectangle(panel.x + 40f, panel.y + 76f, panelW - 80f, 44f);
        }

        HowToPlayAction action = HowToPlayAction.NONE;
        if (consumeClick(backButton)) {
            action = rulesOpenedFromPause ? HowToPlayAction.BACK_TO_PAUSE : HowToPlayAction.BACK_TO_MENU;
        }
        if (rulesOpenedFromStart && consumeClick(continueButton)) {
            action = HowToPlayAction.CONTINUE_TO_AVATAR;
        }

        drawScreenPanel(panel);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.45f, 0.78f, 0.5f);
        shapeRenderer.rect(panel.x + 8f, panel.y + panelH - 48f, panelW - 16f, 36f);
        shapeRenderer.end();

        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));
        if (rulesOpenedFromStart) {
            drawActionButton(continueButton, new Color(0.16f, 0.62f, 0.2f, 1f));
        }

        batch.begin();

        float textX = panel.x + 52f;
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;

        font.getData().setScale(1.2f, 1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HOW TO PLAY", panel.x + 40f, panel.y + panelH - 22f);

        font.getData().setScale(1f, 1f);
        // TIGHTENED SPACING: Reduced the subtracted numbers to pull the text higher
        font.draw(batch, "1.  Move with WASD or arrow keys.", textX, panel.y + panelH - 80f);
        font.draw(batch, "2.  Collect food items to fill your plate:", textX, panel.y + panelH - 120f);

        float iconSize   = 32f;
        float usableW    = panelW - 80f - 20f;
        float colSpacing = usableW / 4f;
        float iconStartX = textX + 20f;
        // Moved the row of icons up
        float iconY      = panel.y + panelH - 170f;

        String[] ranges = {"2 - 4", "1 - 3", "1 - 2", "0 - 1"};
        Texture[] icons = {vegeIcon, proteinIcon, carbIcon, oilIcon};

        for (int i = 0; i < 4; i++) {
            float colX = iconStartX + colSpacing * i;
            if (icons[i] != null) {
                batch.draw(icons[i], colX, iconY, iconSize, iconSize);
            }
            font.getData().setScale(1.2f, 1.2f);
            font.setColor(Color.WHITE);
            font.draw(batch, ranges[i], colX + iconSize + 6f, iconY + iconSize - 4f);
        }

        font.getData().setScale(1f, 1f);
        // Pulled Step 3 and Step 4 significantly higher to avoid the buttons
        font.draw(batch, "3.  Press  ENTER  to submit plate, repeat until time ends.", textX, panel.y + panelH - 220f);
        font.draw(batch, "4.  Press  R  to reset plate,  ESC  to pause/resume.", textX, panel.y + panelH - 260f);

        String backText = rulesOpenedFromPause ? "Back to Pause Menu" : "Back to Main Menu";
        font.draw(batch, backText, backButton.x + 20f, backButton.y + 28f);

        if (rulesOpenedFromStart) {
            font.draw(batch, "Continue to Player Setup", continueButton.x + 20f, continueButton.y + 28f);
        }

        drawStatus(20f, 24f);

        font.getData().setScale(originalScaleX, originalScaleY);
        batch.end();

        return action;
    }

    public LeaderboardEntryAction renderLeaderboardEntry(int finalScore, Texture selectedAvatarTexture, String playerNameInput, boolean leaderboardNameEditing) {
        applyFullScreenProjection();

        float width   = Gdx.graphics.getWidth();
        float height  = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW  = Math.min(760f, width - 80f);
        float panelH  = Math.min(580f, height - 60f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        float pad  = 40f;
        float btnW = panelW - pad * 2f;
        float btnH = 44f;
        float gap  = 12f;

        Rectangle backButton   = new Rectangle(panel.x + pad, panel.y + 28f,                 btnW, btnH);
        Rectangle submitButton = new Rectangle(panel.x + pad, backButton.y   + btnH + gap,   btnW, btnH);
        Rectangle uploadButton = new Rectangle(panel.x + pad, submitButton.y + btnH + gap,   btnW, btnH);
        Rectangle nameField    = new Rectangle(panel.x + pad, uploadButton.y + btnH + gap,   btnW, 42f);

        LeaderboardEntryAction action = LeaderboardEntryAction.NONE;
        if (consumeClick(nameField))    action = LeaderboardEntryAction.ENABLE_NAME_EDIT;
        if (consumeClick(uploadButton)) action = LeaderboardEntryAction.REQUEST_UPLOAD;
        if (consumeClick(submitButton)) action = LeaderboardEntryAction.SUBMIT;
        if (consumeClick(backButton))   action = LeaderboardEntryAction.BACK_TO_MENU;

        drawScreenPanel(panel);

        float dividerY = nameField.y + 42f + gap * 2f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.72f, 0.78f, 0.9f, 0.25f);
        shapeRenderer.rect(panel.x + pad, dividerY, panelW - pad * 2f, 1f);
        shapeRenderer.end();

        drawTextInputField(nameField, leaderboardNameEditing);
        drawActionButton(uploadButton, new Color(0.1f,  0.45f, 0.78f, 1f));
        drawActionButton(submitButton, new Color(0.13f, 0.47f, 0.2f,  1f));
        drawActionButton(backButton,   new Color(0.2f,  0.2f,  0.25f, 1f));

        float avatarSize  = 64f;
        float avatarX     = panel.x + pad;
        float cardCenterY = (dividerY + panel.y + panelH) / 2f;
        float avatarY     = cardCenterY - avatarSize / 2f;
        float scoreBlockX = avatarX + avatarSize + 24f;
        float scoreTopY   = cardCenterY + 30f;

        float titleBarH = 32f;
        float titleBarY = panel.y + panelH - titleBarH - 8f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.45f, 0.78f, 0.4f);
        shapeRenderer.rect(panel.x + 8f, titleBarY, panelW - 16f, titleBarH);
        shapeRenderer.setColor(0.06f, 0.08f, 0.14f, 1f);
        shapeRenderer.rect(avatarX - 4f, avatarY - 4f, avatarSize + 8f, avatarSize + 8f);
        shapeRenderer.setColor(0.72f, 0.78f, 0.9f, 0.6f);
        shapeRenderer.rect(avatarX - 5f, avatarY - 5f,              avatarSize + 10f, 1f);
        shapeRenderer.rect(avatarX - 5f, avatarY + avatarSize + 4f, avatarSize + 10f, 1f);
        shapeRenderer.rect(avatarX - 5f, avatarY - 5f,              1f, avatarSize + 10f);
        shapeRenderer.rect(avatarX + avatarSize + 4f, avatarY - 5f, 1f, avatarSize + 10f);
        shapeRenderer.end();

        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, "RUN COMPLETE", panel.x + pad, titleBarY + titleBarH - 8f);

        if (selectedAvatarTexture != null) {
            batch.draw(selectedAvatarTexture, avatarX, avatarY, avatarSize, avatarSize);
        } else {
            font.setColor(new Color(0.55f, 0.55f, 0.65f, 1f));
            font.draw(batch, "[ no avatar ]", avatarX + 4f, avatarY + avatarSize - 8f);
            font.setColor(Color.WHITE);
        }

        font.setColor(new Color(0.72f, 0.78f, 0.9f, 1f));
        font.draw(batch, "FINAL SCORE", scoreBlockX, scoreTopY);
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf(finalScore), scoreBlockX, scoreTopY - 22f);

        font.setColor(new Color(0.65f, 0.68f, 0.75f, 1f));
        font.draw(batch, "Enter your name and upload your", scoreBlockX, scoreTopY - 50f);
        font.draw(batch, "avatar to save your score.",      scoreBlockX, scoreTopY - 66f);
        font.setColor(Color.WHITE);

        font.setColor(new Color(0.72f, 0.78f, 0.9f, 1f));
        font.draw(batch, "YOUR NAME", panel.x + pad, nameField.y + 42f + 18f);
        font.setColor(Color.WHITE);

        boolean isEmpty  = playerNameInput.isBlank();
        String shownName = isEmpty ? "Type your name here..." : playerNameInput;
        if (leaderboardNameEditing && ((System.currentTimeMillis() / 350L) % 2L == 0L)) {
            shownName += "_";
        }
        font.setColor(isEmpty ? new Color(0.45f, 0.48f, 0.55f, 1f) : Color.WHITE);
        font.draw(batch, shownName, nameField.x + 14f, nameField.y + 28f);
        font.setColor(Color.WHITE);

        font.draw(batch, "Upload / Change Avatar", uploadButton.x + 16f, uploadButton.y + 28f);
        font.draw(batch, "Submit to Leaderboard",  submitButton.x + 16f, submitButton.y + 28f);
        font.draw(batch, "Back to Main Menu",       backButton.x  + 16f, backButton.y  + 28f);

        drawStatus(20f, 24f);
        batch.end();
        return action;
    }

    public LeaderboardViewAction renderLeaderboardView(List<? extends LeaderboardRow> rows, boolean leaderboardOpenedFromMenu) {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(800f, width - 80f);
        float panelH = Math.min(540f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        String footerLabel = leaderboardOpenedFromMenu ? "Back to Main Menu" : "Play Again";
        Rectangle footerButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        LeaderboardViewAction action = consumeClick(footerButton) ? LeaderboardViewAction.FOOTER_CLICKED : LeaderboardViewAction.NONE;

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

    public void dispose() {
        if (easyDifficultyIcon != null) easyDifficultyIcon.dispose();
        if (normalDifficultyIcon != null) normalDifficultyIcon.dispose();
        if (hardDifficultyIcon != null) hardDifficultyIcon.dispose();
        if (timerIcon != null) timerIcon.dispose();
        if (playingBackgroundTexture != null) playingBackgroundTexture.dispose();
        if (vegeIcon != null) vegeIcon.dispose();
        if (proteinIcon != null) proteinIcon.dispose();
        if (carbIcon != null) carbIcon.dispose();
        if (oilIcon != null) oilIcon.dispose();
    }

    private Texture loadTextureOrNull(String assetName) {
        if (!Gdx.files.internal(assetName).exists()) return null;
        return new Texture(Gdx.files.internal(assetName));
    }

    private void drawDifficultyOption(SpriteBatch batch, Texture icon, Rectangle bounds, String label) {
        float textX = bounds.x + 20f;
        if (icon != null) {
            float iconSize = Math.min(30f, bounds.height - 12f);
            float iconX = bounds.x + 12f;
            float iconY = bounds.y + (bounds.height - iconSize) / 2f;
            batch.draw(icon, iconX, iconY, iconSize, iconSize);
            textX = iconX + iconSize + 12f;
        }
        font.draw(batch, label, textX, bounds.y + 30f);
    }
}