package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.entities.MovementEntity;
import com.sit.inf1009.project.engine.managers.MovementManager;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture image;

    private MovementManager movementManager;
    private MovementEntity player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png"); // or "player.jpg"

        movementManager = new MovementManager();
        player = new MovementEntity(
                140,
                210,
                200,
                true,
                image.getWidth(),
                image.getHeight()
        );
        
        movementManager.add(player);
    }

    @Override
    public void render() {
        double dt = Gdx.graphics.getDeltaTime();

        movementManager.updateAll(dt);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(image, player.getX(), player.getY());
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
