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
import com.sit.inf1009.project.engine.core.Scene;
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

    private GameplayLoopOrchestrator() {
    }

    public static boolean togglePauseOnSpace(boolean paused) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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

    public static void renderWorldAndHud(ShapeRenderer shapeRenderer,
                                         SpriteBatch batch,
                                         BitmapFont font,
                                         EntityManager entityManager,
                                         SceneManager sceneManager,
                                         GameplayRuntime gameplayRuntime,
                                         GameSession gameSession,
                                         DifficultyPreset difficultyPreset,
                                         AppUiRenderer appUiRenderer) {
        sceneManager.render(null);
        Texture playingBackground = appUiRenderer.getPlayingBackgroundTexture();
        if (playingBackground != null) {
            batch.begin();
            batch.draw(playingBackground, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getEntities()) {
            if (e.getTexture() != null) continue;
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.setColor(gameplayRuntime.getEntityRenderColor(e));
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }

        float hudHeight = gameplayRuntime.getHudBlockHeight();
        float hudY = Gdx.graphics.getHeight() - hudHeight;
        float hudX = 0f;
        float hudW = Math.max(1f, Gdx.graphics.getWidth());
        float hudRight = hudX + hudW;

        shapeRenderer.setColor(0f, 0f, 0f, 0.92f);
        shapeRenderer.rect(hudX, hudY, hudW, hudHeight);

        float timerW = Math.min(170f, hudW * 0.28f);
        shapeRenderer.setColor(0.08f, 0.86f, 0.12f, 1f);
        shapeRenderer.rect(hudX + 2f, hudY + 2f, timerW, hudHeight - 4f);
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

        float hudScale = gameplayRuntime.getHudScaleFactor();
        float textInsetX = 12f * hudScale;
        float textTopInset = 10f * hudScale;
        float textY = hudY + hudHeight - textTopInset;
        float iconSize = 16f * hudScale;
        float iconY = hudY + ((hudHeight - iconSize) * 0.5f);
        float counterStep = 38f * hudScale;
        float counterGroupWidth = counterStep * 4f;
        float countersX = hudRight - counterGroupWidth - (12f * hudScale);

        float originalFontScaleX = font.getData().scaleX;
        float originalFontScaleY = font.getData().scaleY;
        font.getData().setScale(hudScale);
        font.setColor(Color.WHITE);
        float timerTextX = hudX + textInsetX;
        Texture timerIcon = appUiRenderer.getTimerIcon();
        if (timerIcon != null) {
            float timerIconSize = iconSize * 1.05f;
            float timerIconX = hudX + (8f * hudScale);
            float timerIconY = hudY + ((hudHeight - timerIconSize) * 0.5f);
            batch.draw(timerIcon, timerIconX, timerIconY, timerIconSize, timerIconSize);
            timerTextX = timerIconX + timerIconSize + (8f * hudScale);
        }
        font.draw(batch, (int) Math.ceil(gameSession.getTimer()) + "s", timerTextX, textY);

        String scoreText = "Score: " + gameSession.getScore();
        float scoreX = hudX + timerW + (22f * hudScale);
        font.draw(batch, scoreText, scoreX, textY);

        HUD_GLYPH.setText(font, scoreText);
        float difficultyX = scoreX + HUD_GLYPH.width + (28f * hudScale);
        float maxDifficultyX = countersX - (60f * hudScale);
        if (difficultyX > maxDifficultyX) {
            difficultyX = maxDifficultyX;
        }
        Texture difficultyIcon = appUiRenderer.getDifficultyIcon(difficultyPreset);
        float difficultyTextX = difficultyX;
        if (difficultyIcon != null) {
            float difficultyIconSize = iconSize * 1.05f;
            float difficultyIconX = difficultyX;
            float difficultyIconY = hudY + ((hudHeight - difficultyIconSize) * 0.5f);
            batch.draw(difficultyIcon, difficultyIconX, difficultyIconY, difficultyIconSize, difficultyIconSize);
            difficultyTextX = difficultyIconX + difficultyIconSize + (8f * hudScale);
        }
        font.draw(batch, difficultyPreset.getLabel(), difficultyTextX, textY);

        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.VEGETABLE),
                gameSession.getVegetableCount(), countersX, iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.PROTEIN),
                gameSession.getProteinCount(), countersX + counterStep, iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.CARBOHYDRATE),
                gameSession.getCarbCount(), countersX + (counterStep * 2f), iconY, iconSize, counterStep);
        drawHudFoodCounter(batch, font, gameplayRuntime.getFoodTexture(FoodCategory.OIL),
                gameSession.getOilCount(), countersX + (counterStep * 3f), iconY, iconSize, counterStep);

        font.getData().setScale(originalFontScaleX, originalFontScaleY);
        appUiRenderer.drawStatus(20f, 24f);
        batch.end();
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
        float panelW = 350f;
        float panelH = 340f;
        float centerX = width / 2f;
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        Rectangle resumeBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 120f, panelW - 80f, 44f);
        Rectangle restartBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 180f, panelW - 80f, 44f);
        Rectangle rulesBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 240f, panelW - 80f, 44f);
        Rectangle quitBtn = new Rectangle(panel.x + 40f, panel.y + 40f, panelW - 80f, 44f);

        PauseAction action = PauseAction.NONE;
        if (appUiRenderer.consumeClick(resumeBtn)) action = PauseAction.RESUME;
        if (appUiRenderer.consumeClick(restartBtn)) action = PauseAction.RESTART;
        if (appUiRenderer.consumeClick(rulesBtn)) action = PauseAction.OPEN_RULES;
        if (appUiRenderer.consumeClick(quitBtn)) action = PauseAction.QUIT_TO_MENU;

        appUiRenderer.drawScreenPanel(panel);
        appUiRenderer.drawActionButton(resumeBtn, new Color(0.16f, 0.62f, 0.2f, 1f));
        appUiRenderer.drawActionButton(restartBtn, new Color(0.1f, 0.45f, 0.78f, 1f));
        appUiRenderer.drawActionButton(rulesBtn, new Color(0.6f, 0.4f, 0.1f, 1f));
        appUiRenderer.drawActionButton(quitBtn, new Color(0.75f, 0.22f, 0.22f, 1f));

        batch.begin();
        font.draw(batch, "GAME PAUSED", panel.x + panelW / 2f - 45f, panel.y + panelH - 30f);
        font.draw(batch, "Resume Game", resumeBtn.x + 20f, resumeBtn.y + 28f);
        font.draw(batch, "Restart Game", restartBtn.x + 20f, restartBtn.y + 28f);
        font.draw(batch, "How to Play", rulesBtn.x + 20f, rulesBtn.y + 28f);
        font.draw(batch, "Quit to Main Menu", quitBtn.x + 20f, quitBtn.y + 28f);
        batch.end();

        return action;
    }
}
