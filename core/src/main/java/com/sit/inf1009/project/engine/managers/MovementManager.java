package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.sit.inf1009.project.engine.interfaces.MovementInterface;

public class MovementManager {

    private final List<MovementInterface> movableEntities = new ArrayList<>();

    public void add(MovementInterface entity) {
        movableEntities.add(entity);
    }

    public void remove(MovementInterface entity) {
        movableEntities.remove(entity);
    }

    public void updateAll(double dt) {
        for (MovementInterface entity : movableEntities) {
            entity.update(dt);
        }
    }

    public void clear() {
        movableEntities.clear();
    }
}
