package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sit.inf1009.project.engine.entities.Entity;

public class EntityManager {

    private final List<Entity> entityList = new ArrayList<>();
    private final Set<Entity> pendingRemoval = new LinkedHashSet<>();

    public void addEntity(Entity e) {
        if (e == null) return;
        entityList.add(e);
    }

    public void removeEntity(Entity e) {
        if (e == null) return;
        entityList.remove(e);
    }

    // CollisionManager calls this (SAFE during iteration)
    public void queueRemove(Entity e) {
        if (e == null) return;
        pendingRemoval.add(e);
    }

    // Call once per frame after managers update
    public void flushRemovals() {
        if (pendingRemoval.isEmpty()) return;

        for (Entity e : pendingRemoval) {
            removeEntity(e);
        }
        pendingRemoval.clear();
    }

    // CollisionManager reads from this
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entityList);
    }

    public int getCount() {
        return entityList.size();
    }

    public void clear() {
        entityList.clear();
        pendingRemoval.clear();
    }
}