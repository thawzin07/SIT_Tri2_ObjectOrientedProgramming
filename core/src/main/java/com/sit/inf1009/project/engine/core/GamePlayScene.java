package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.entities.Entity;
import java.util.List;

public class GamePlayScene extends Scene {

    private BitmapFont font;

    public GamePlayScene() {
        super("Gameplay", Color.BLACK);
    }

    @Override
    public void create() {
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void update(float dt, List<Entity> entities) {
        super.update(dt, entities); // handles clamping + timeAlive
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "GAMEPLAY - Coming Soon!", 
                  Gdx.graphics.getWidth() / 2f - 180,
                  Gdx.graphics.getHeight() / 2f);
        batch.end();
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
    }
}
