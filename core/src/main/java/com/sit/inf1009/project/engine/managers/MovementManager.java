package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.sit.inf1009.project.engine.entities.MovementEntity;
import com.sit.inf1009.project.engine.interfaces.MovementInterface;

public class MovementManager {

    private final List<MovementInterface> movableEntities = new ArrayList<>();

    // Spawn settings
    private double spawnTimer = 0;
    private double spawnInterval = 0.5; // seconds
    private int maxDroplets = 15;

    // Droplet settings (match your droplet.png size if you want)
    private float dropletW = 24;
    private float dropletH = 24;
    private double dropletSpeed = 200;

    public void add(MovementInterface entity) {
        movableEntities.add(entity);
    }

    public void remove(MovementInterface entity) {
        movableEntities.remove(entity);
    }

    public void updateAll(double dt) {
        // Spawn droplets over time
        spawnTimer += dt;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            if (countDroplets() < maxDroplets) {
                spawnDroplet();
            }
        }

        for (MovementInterface entity : movableEntities) {
            entity.update(dt);
        }
    }

    private void spawnDroplet() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        double x = Math.random() * (screenW - dropletW);
        double y = screenH; // start slightly above top

        MovementEntity droplet = new MovementEntity(
                x, y,
                dropletSpeed,
                false,          // AI controlled
                dropletW, dropletH
        );

        add(droplet);
    }

    private int countDroplets() {
        int c = 0;
        for (MovementInterface e : movableEntities) {
            if (e instanceof MovementEntity me && !me.isPlayerControlled()) c++;
        }
        return c;
    }

    public void clear() {
        movableEntities.clear();
    }

    // Needed so Main can render droplets
    public List<MovementInterface> getEntities() {
        return movableEntities;
    }
}
