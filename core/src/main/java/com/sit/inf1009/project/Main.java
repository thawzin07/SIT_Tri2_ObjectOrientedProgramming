package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sit.inf1009.project.engine.entities.*;
import com.sit.inf1009.project.engine.managers.MovementManager;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private final List<Entity> entities = new ArrayList<>();
    private MovementManager movementManager;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        movementManager = new MovementManager();

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Create IO system
        InputOutputManager ioManager = new InputOutputManager();
        new KeyboardInputHandler(ioManager); // hooks into LibGDX input

        // 1) Player square (WASD via IO)
        PlayerSquareEntity player = new PlayerSquareEntity(1, 200, 200, 30f);
        player.setMovement(new PlayerMovement(ioManager, 250)); // PASS ioManager
        entities.add(player);
        movementManager.addMovable(player);

        // 2) AI circles
        Random rng = new Random();
        for (int i = 0; i < 8; i++) {
            double x = 100 + rng.nextInt(500);
            double y = 100 + rng.nextInt(300);
            AICircleEntity npc = new AICircleEntity(100 + i, x, y, 8f);

            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;

            npc.setMovement(new AIMovement(120, dirX, dirY));
            entities.add(npc);
            movementManager.addMovable(npc);
        }
    }


    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        double dt = Gdx.graphics.getDeltaTime();
        movementManager.updateAll(dt);

        // FYI text
        batch.begin();
        font.draw(batch, "Move with WASD", 20, Gdx.graphics.getHeight() - 20);
        batch.end();

        // Draw shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : entities) {
            if (e instanceof PlayerSquareEntity p) {
                float size = p.getSize();
                shapeRenderer.rect((float) p.getXPosition(), (float) p.getYPosition(), size, size);
            } else if (e instanceof AICircleEntity c) {
                shapeRenderer.circle((float) c.getXPosition(), (float) c.getYPosition(), c.getRadius());
            }
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
