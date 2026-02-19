package com.sit.inf1009.project.engine.managers;
import java.util.ArrayList;
import java.util.List;
import com.sit.inf1009.project.engine.entities.Entity;

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
            if (e == null) continue;
            if (e.getMovement() == null) continue;
            e.getMovement().update(e, dt);
        }
    }

    public void clear() {
        movables.clear();
    }
}