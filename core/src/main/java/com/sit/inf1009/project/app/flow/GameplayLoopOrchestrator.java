package com.sit.inf1009.project.app.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.sit.inf1009.project.app.DifficultyPreset;
import com.sit.inf1009.project.app.runtime.GameplayRuntime;
import com.sit.inf1009.project.app.ui.AppUiRenderer;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.FoodCategory;
import com.sit.inf1009.project.game.domain.GameSession;

public final class GameplayLoopOrchestrator {
    private static final GlyphLayout HUD_GLYPH = new GlyphLayout();

    public enum PauseAction {
        NONE,
        RESUME,
        RESTART,
        OPEN_RULES,
        QUIT_TO_MENU
    }

    public enum HudAction {
        NONE,
        PAUSE_CLICKED
    }

    private GameplayLoopOrchestrator() {
    }

    public static boolean togglePauseOnEsc(boolean paused) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return !paused;
        }
        return paused;
    }

    public static boolean tickSimulation(float dt,
                                         boolean paused,
                                         MovementManager movementManager,
                                         SceneManager sceneManager,
                                         EntityManager entityManager,
                                         CollisionManager collisionManager,
                                         GameplayRuntime gameplayRuntime,
                                         DifficultyConfig difficultyConfig,
                                         GameSession gameSession) {
        if (paused) {
            return false;
        }

        gameplayRuntime.configureMovementBoundsForCurrentViewport();
        movementManager.updateAll(dt);
        sceneManager.update(dt, entityManager.getEntities());
        gameplayRuntime.clampEntitiesToPlayableArea();
        collisionManager.update();
        entityManager.flushRemovals();
        gameplayRuntime.ensureFoodDiversityAndCount(difficultyConfig);

        float nextTimer = Math.max(0f, gameSession.getTimer() - dt);
        gameSession.setTimer(nextTimer);
        return nextTimer <= 0f;
    }

    public static HudAction renderWorldAndHud(ShapeRenderer shapeRenderer,
                                              SpriteBatch batch,
                                              BitmapFont font,
                                              EntityManager entityManager,
                                              SceneManager sceneManager,
                                              GameplayRuntime gameplayRuntime,
                                              GameSession gameSession,
                                              DifficultyPreset difficultyPreset,
                                              AppUiRenderer appUiRenderer) {
        sceneManager.render(batch);

        Texture playingBackground = appUiRenderer.getPlayingBackgroundTexture();
        if (playingBackground != null) {
            batch.begin();
            batch.draw(playingBackground, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }

        float hudHeight = gameplayRuntime.getHudBlockHeight();
        float hudY = Gdx.graphics.getHeight() - hudHeight;
        float hudX = 0f;
        float hudW = Math.max(1f, Gdx.graphics.getWidth());
        float hudRight = hudX + hudW;
        float hudScale = gameplayRuntime.getHudScaleFactor();

        float outerPad = 6f * hudScale;
        float innerPad = 10f * hudScale;
        float iconSize = 16f * hudScale;
        float iconY = hudY + ((hudHeight - iconSize) * 0.5f);

        float timerChipW = Math.min(175f * hudScale, hudW * 0.26f);
        float timerChipH = hudHeight - (outerPad * 2f);
        float timerChipX = hudX + outerPad;
        float timerChipY = hudY + outerPad;

        float counterStep = 55f * hudScale;
        float foodGroupW = (counterStep * 4f) + (22f * hudScale);
        float countersX = hudRight - foodGroupW - (14f * hudScale);
        float foodGroupX = countersX - (8f * hudScale);
        float foodGroupY = hudY + (7f * hudScale);
        float foodGroupH = hudHeight - (14f * hudScale);

        float pauseBtnW = 88f * hudScale;
        float pauseBtnH = hudHeight - (outerPad * 2f);
        float pauseBtnX = foodGroupX - pauseBtnW - (12f * hudScale);
        float pauseBtnY = hudY + outerPad;
        Rectangle pauseHudBtn = new Rectangle(pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : entityManager.getEntities()) {
            if (e.getTexture() != null) continue;

            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.setColor(gameplayRuntime.getEntityRenderColor(e));
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }

        // HUD shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.rect(hudX, hudY - (4f * hudScale), hudW, hudHeight + (4f * hudScale));

        // Main HUD bar
        shapeRenderer.setColor(0.09f, 0.07f, 0.11f, 0.96f);
        shapeRenderer.rect(hudX, hudY, hudW, hudHeight);

        // Top highlight
        shapeRenderer.setColor(1f, 1f, 1f, 0.05f);
        shapeRenderer.rect(hudX, hudY + hudHeight - (3f * hudScale), hudW, 3f * hudScale);

        // Bottom divider
        shapeRenderer.setColor(0f, 0f, 0f, 0.25f);
        shapeRenderer.rect(hudX, hudY, hudW, 2f * hudScale);

        // Timer capsule
        drawCapsule(shapeRenderer, timerChipX, timerChipY, timerChipW, timerChipH,
                new Color(0.16f, 0.43f, 0.18f, 1f));
        drawCapsule(shapeRenderer, timerChipX + (3f * hudScale), timerChipY + (3f * hudScale),
                timerChipW - (6f * hudScale), timerChipH - (6f * hudScale),
                new Color(0.76f, 0.94f, 0.66f, 1f));

        // Timer gloss
        drawCapsule(shapeRenderer,
                timerChipX + (3f * hudScale),
                timerChipY + (timerChipH * 0.52f),
                timerChipW - (6f * hudScale),
                (timerChipH - (6f * hudScale)) * 0.32f,
                new Color(1f, 1f, 1f, 0.12f));

        // One soft food strip instead of 4 ugly squares
        drawCapsule(shapeRenderer, foodGroupX, foodGroupY, foodGroupW, foodGroupH,
                new Color(1f, 1f, 1f, 0.07f));
        drawCapsule(shapeRenderer, foodGroupX + (1.5f * hudScale), foodGroupY + (1.5f * hudScale),
                foodGroupW - (3f * hudScale), foodGroupH - (3f * hudScale),
                new Color(0f, 0f, 0f, 0.10f));

        // Pause capsule
        drawCapsule(shapeRenderer, pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH,
                new Color(0.35f, 0.20f, 0.08f, 1f));
        drawCapsule(shapeRenderer, pauseBtnX + (3f * hudScale), pauseBtnY + (3f * hudScale),
                pauseBtnW - (6f * hudScale), pauseBtnH - (6f * hudScale),
                new Color(0.84f, 0.67f, 0.38f, 1f));

        // Pause gloss
        drawCapsule(shapeRenderer,
                pauseBtnX + (3f * hudScale),
                pauseBtnY + (pauseBtnH * 0.54f),
                pauseBtnW - (6f * hudScale),
                (pauseBtnH - (6f * hudScale)) * 0.24f,
                new Color(1f, 1f, 1f, 0.10f));

        // Pause icon bars
        float barW = 5f * hudScale;
        float barH = 18f * hudScale;
        float barGap = 5f * hudScale;
        float barsX = pauseBtnX + (13f * hudScale);
        float barsY = pauseBtnY + ((pauseBtnH - barH) / 2f);

        shapeRenderer.setColor(0.24f, 0.13f, 0.04f, 1f);
        shapeRenderer.rect(barsX, barsY, barW, barH);
        shapeRenderer.rect(barsX + barW + barGap, barsY, barW, barH);

        shapeRenderer.end();

        batch.begin();

        for (Entity e : entityManager.getEntities()) {
            Texture texture = e.getTexture();
            if (texture == null) continue;

            CollidableComponent c = e.getCollidable();
            float size = (c != null) ? (float) c.getCollisionRadius() * 2f : 32f;
            float x = (float) e.getXPosition() - (size / 2f);
            float y = (float) e.getYPosition() - (size / 2f);
            batch.draw(texture, x, y, size, size);
        }

        float originalFontScaleX = font.getData().scaleX;
        float originalFontScaleY = font.getData().scaleY;

        font.getData().setScale(hudScale);
        font.setColor(Color.WHITE);

        float textY = hudY + hudHeight - innerPad;

        // Timer text
        float timerTextX = timerChipX + (12f * hudScale);
        Texture timerIcon = appUiRenderer.getTimerIcon();
        if (timerIcon != null) {
            float timerIconSize = iconSize * 1.1f;
            float timerIconX = timerChipX + (10f * hudScale);
            float timerIconY = hudY + ((hudHeight - timerIconSize) * 0.5f);
            batch.draw(timerIcon, timerIconX, timerIconY, timerIconSize, timerIconSize);
            timerTextX = timerIconX + timerIconSize + (8f * hudScale);
        }
        font.getData().setScale(hudScale * 1.3f);
        font.setColor(Color.BLACK);
        font.draw(batch, (int) Math.ceil(gameSession.getTimer()) + "s", timerTextX, textY);

        // Score
        font.getData().setScale(hudScale * 1.3f);
        font.setColor(Color.WHITE);
        String scoreText = "Score: " + gameSession.getScore();
        float scoreX = timerChipX + timerChipW + (18f * hudScale);
        font.draw(batch, scoreText, scoreX, textY);

        // Difficulty
        HUD_GLYPH.setText(font, scoreText);
        float difficultyX = scoreX + HUD_GLYPH.width + (22f * hudScale);
        float maxDifficultyX = pauseBtnX - (82f * hudScale);
        if (difficultyX > maxDifficultyX) {
            difficultyX = maxDifficultyX;
        }

        Texture difficultyIcon = appUiRenderer.getDifficultyIcon(difficultyPreset);
        float difficultyTextX = difficultyX;
        if (difficultyIcon != null) {
            float difficultyIconSize = iconSize * 1.08f;
            float difficultyIconX = difficultyX;
            float difficultyIconY = hudY + ((hudHeight - difficultyIconSize) * 0.5f);
            batch.draw(difficultyIcon, difficultyIconX, difficultyIconY, difficultyIconSize, difficultyIconSize);
            difficultyTextX = difficultyIconX + difficultyIconSize + (8f * hudScale);
        }
        font.draw(batch, difficultyPreset.getLabel(), difficultyTextX, textY);

        // Food counters
        font.getData().setScale(hudScale * 1.3f);
        font.setColor(Color.YELLOW);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.VEGETABLE),
                gameSession.getVegetableCount(), countersX, iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.PROTEIN),
                gameSession.getProteinCount(), countersX + counterStep, iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.CARBOHYDRATE),
                gameSession.getCarbCount(), countersX + (counterStep * 2f), iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.OIL),
                gameSession.getOilCount(), countersX + (counterStep * 3f), iconY, iconSize, counterStep);

        // Pause label
        font.setColor(new Color(0.22f, 0.12f, 0.04f, 1f));
        float pauseLabelX = barsX + (barW * 2f) + barGap + (9f * hudScale);
        font.draw(batch, "Pause", pauseLabelX, textY);

        font.getData().setScale(originalFontScaleX, originalFontScaleY);
        font.setColor(Color.WHITE);

        appUiRenderer.drawStatus(20f, 24f);
        batch.end();

        HudAction hudAction = HudAction.NONE;
        if (appUiRenderer.consumeClick(pauseHudBtn)) {
            hudAction = HudAction.PAUSE_CLICKED;
        }
        return hudAction;
    }

    private static float drawHudFoodCounter(SpriteBatch batch,
                                            BitmapFont font,
                                            Texture iconTexture,
                                            int count,
                                            float startX,
                                            float iconY,
                                            float iconSize,
                                            float counterStep) {
        float x = startX;

        if (iconTexture != null) {
            batch.draw(iconTexture, x, iconY, iconSize, iconSize);
        } else {
            font.draw(batch, "[]", x, iconY + iconSize);
        }

        font.draw(batch, String.valueOf(count), x + iconSize + 6f, iconY + iconSize - 1f);
        return x + counterStep;
    }

    public static PauseAction renderPauseMenu(AppUiRenderer appUiRenderer,
                                              SpriteBatch batch,
                                              BitmapFont font) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float panelW = 380f;
        float panelH = 360f;
        float centerX = width / 2f;
        Rectangle panel = new Rectangle(centerX - (panelW / 2f), (height - panelH) / 2f, panelW, panelH);

        Rectangle resumeBtn = new Rectangle(panel.x + 38f, panel.y + panelH - 122f, panelW - 76f, 48f);
        Rectangle restartBtn = new Rectangle(panel.x + 38f, panel.y + panelH - 186f, panelW - 76f, 48f);
        Rectangle rulesBtn = new Rectangle(panel.x + 38f, panel.y + panelH - 250f, panelW - 76f, 48f);
        Rectangle quitBtn = new Rectangle(panel.x + 38f, panel.y + 38f, panelW - 76f, 48f);

        PauseAction action = PauseAction.NONE;
        if (appUiRenderer.consumeClick(resumeBtn)) action = PauseAction.RESUME;
        if (appUiRenderer.consumeClick(restartBtn)) action = PauseAction.RESTART;
        if (appUiRenderer.consumeClick(rulesBtn)) action = PauseAction.OPEN_RULES;
        if (appUiRenderer.consumeClick(quitBtn)) action = PauseAction.QUIT_TO_MENU;

        appUiRenderer.drawScreenPanel(panel);
        appUiRenderer.drawActionButton(resumeBtn, new Color(0.22f, 0.68f, 0.28f, 1f));
        appUiRenderer.drawActionButton(restartBtn, new Color(0.19f, 0.48f, 0.85f, 1f));
        appUiRenderer.drawActionButton(rulesBtn, new Color(0.86f, 0.66f, 0.18f, 1f));
        appUiRenderer.drawActionButton(quitBtn, new Color(0.84f, 0.27f, 0.27f, 1f));

        float originalFontScaleX = font.getData().scaleX;
        float originalFontScaleY = font.getData().scaleY;

        batch.begin();

        font.getData().setScale(1.15f, 1.15f);
        drawCenteredText(batch, font, "GAME PAUSED", panel.x + (panelW / 2f), panel.y + panelH - 26f);

        font.getData().setScale(0.9f, 0.9f);
        drawCenteredText(batch, font, "Choose an option", panel.x + (panelW / 2f), panel.y + panelH - 52f);

        font.getData().setScale(1f, 1f);
        drawCenteredText(batch, font, "Resume Game", resumeBtn.x + (resumeBtn.width / 2f), resumeBtn.y + 31f);
        drawCenteredText(batch, font, "Restart Game", restartBtn.x + (restartBtn.width / 2f), restartBtn.y + 31f);
        drawCenteredText(batch, font, "How to Play", rulesBtn.x + (rulesBtn.width / 2f), rulesBtn.y + 31f);
        drawCenteredText(batch, font, "Quit to Main Menu", quitBtn.x + (quitBtn.width / 2f), quitBtn.y + 31f);

        font.getData().setScale(originalFontScaleX, originalFontScaleY);
        batch.end();

        return action;
    }

    private static void drawCenteredText(SpriteBatch batch,
                                         BitmapFont font,
                                         String text,
                                         float centerX,
                                         float baselineY) {
        HUD_GLYPH.setText(font, text);
        font.draw(batch, text, centerX - (HUD_GLYPH.width / 2f), baselineY);
    }

    private static void drawCapsule(ShapeRenderer shapeRenderer,
                                    float x,
                                    float y,
                                    float width,
                                    float height,
                                    Color color) {
        float radius = Math.min(height / 2f, width / 2f);

        shapeRenderer.setColor(color);

        if (width <= height) {
            shapeRenderer.circle(x + (width / 2f), y + (height / 2f), radius);
            return;
        }

        shapeRenderer.rect(x + radius, y, width - (2f * radius), height);
        shapeRenderer.circle(x + radius, y + radius, radius);
        shapeRenderer.circle(x + width - radius, y + radius, radius);
    }
}
