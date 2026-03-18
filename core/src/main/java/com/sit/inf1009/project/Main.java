package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;

public class Main implements ApplicationListener {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private InputOutputManager ioManager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Wire up all managers
        ioManager                         = new InputOutputManager();
        EntityManager entityManager       = new EntityManager();
        MovementManager movementManager   = new MovementManager();
        CollisionManager collisionManager = new CollisionManager(entityManager, ioManager);

        // SceneManager gets everything — starts at StartMenuScene automatically
        sceneManager = new SceneManager(entityManager, movementManager,
                                        collisionManager, ioManager);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sceneManager.update(Gdx.graphics.getDeltaTime());
        sceneManager.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
        // Also fire through IO system so any IOListener can react
        ioManager.handleEvent(
            new com.sit.inf1009.project.engine.managers.IOEvent(
                com.sit.inf1009.project.engine.managers.IOEvent.Type.WINDOW_RESIZED,
                new int[]{width, height}
            )
        );
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (ioManager != null) ioManager.shutdown();
    }
}