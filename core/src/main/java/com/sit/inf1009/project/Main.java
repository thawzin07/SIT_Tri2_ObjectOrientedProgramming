package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.entities.MovementEntity;
import com.sit.inf1009.project.engine.interfaces.MovementInterface;
import com.sit.inf1009.project.engine.managers.MovementManager;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture bucketTex;
    private Texture dropletTex;

    private MovementManager movementManager;
    private MovementEntity bucket;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // MUST be in lwjgl3/assets/
        bucketTex = new Texture("bucket.png");
        dropletTex = new Texture("droplet.png");

        movementManager = new MovementManager();

        // Create player bucket
        bucket = new MovementEntity(
                200, 120,
                260,                 // speed
                true,                // player controlled
                bucketTex.getWidth(),
                bucketTex.getHeight()
        );

        movementManager.add(bucket);
    }

    @Override
    public void render() {
        double dt = Gdx.graphics.getDeltaTime();

        movementManager.updateAll(dt);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        // Draw bucket
        batch.draw(bucketTex, bucket.getX(), bucket.getY());

        // Draw droplets
        for (MovementInterface e : movementManager.getEntities()) {
            if (e instanceof MovementEntity me && !me.isPlayerControlled()) {
                batch.draw(dropletTex, me.getX(), me.getY());
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bucketTex.dispose();
        dropletTex.dispose();
    }
}
