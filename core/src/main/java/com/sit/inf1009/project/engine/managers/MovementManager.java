package com.sit.inf1009.project.engine.managers;

import com.sit.inf1009.project.engine.entities.Entity;
import java.util.ArrayList;
import java.util.List;

public class MovementManager {
    private final List<Entity> movables = new ArrayList<>();

    public void addMovable(Entity e) {
        if (e != null && e.getMovement() != null) movables.add(e);
    }

    public void removeMovable(Entity e) {
        movables.remove(e);
    }

    public void updateAll(double dt) {
        for (Entity e : movables) {
            e.update(dt);
        }
    }

    public void clear() {
        movables.clear();
    }
}