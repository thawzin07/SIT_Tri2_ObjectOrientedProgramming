package com.sit.inf1009.project.app.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.sit.inf1009.project.game.domain.GameSession;

public final class GameplayLoopOrchestrator {

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

        movementManager.updateAll(dt);
        sceneManager.update(dt, entityManager.getEntities());
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getEntities()) {
            if (e.getTexture() != null) continue;
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.setColor(gameplayRuntime.getEntityRenderColor(e));
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }
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

        font.draw(batch, "Timer: " + (int) Math.ceil(gameSession.getTimer()), 20f, Gdx.graphics.getHeight() - 20f);
        font.draw(batch, "Score: " + gameSession.getScore(), 20f, Gdx.graphics.getHeight() - 38f);
        font.draw(batch, "Difficulty: " + difficultyPreset.getLabel(), 20f, Gdx.graphics.getHeight() - 56f);
        font.draw(batch, "Plate V/P/C/O: " + gameSession.getVegetableCount() + "/"
                + gameSession.getProteinCount() + "/" + gameSession.getCarbCount() + "/"
                + gameSession.getOilCount(), 20f, Gdx.graphics.getHeight() - 74f);

        appUiRenderer.drawStatus(20f, 24f);
        batch.end();
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
