package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.sit.inf1009.project.engine.entities.CollidableEntity;

/**
 * CollisionManager (EXACT per UML)
 *
 * - collidables : List<CollidableEntity>
 * - processedPairs : Set<String>
 *
 * + add(collidable: CollidableEntity) : void
 * + remove(collidable: CollidableEntity) : void
 * + update() : void
 * + getCollidables() : List<CollidableEntity>
 * + getCount() : int
 * + clear() : void
 *
 * Assumes CollidableEntity supports:
 * - int getId()
 * - boolean isCollisionEnabled()
 * - double getCollisionRadius()
 * - double getX(), double getY()
 * - void onCollision(CollidableInterface other)
 */
public class CollisionManager {

    private final List<CollidableEntity> collidables;
    private final Set<String> processedPairs;

    public CollisionManager() {
        this.collidables = new ArrayList<>();
        this.processedPairs = new HashSet<>();
    }

    /**
     * Add a collidable entity to the manager
     */
    public void add(CollidableEntity collidable) {
        if (collidable == null) {
            throw new IllegalArgumentException("collidable cannot be null");
        }
        if (!collidables.contains(collidable)) {
            collidables.add(collidable);
        }
    }

    /**
     * Remove a collidable entity from the manager
     */
    public void remove(CollidableEntity collidable) {
        if (collidable == null) return;
        collidables.remove(collidable);
    }

    /**
     * Update collision detection and handle collisions
     * Also removes entities that have collision disabled (destroyed)
     */
    public void update() {
        processedPairs.clear();

        // Process collisions
        for (int i = 0; i < collidables.size(); i++) {
            CollidableEntity a = collidables.get(i);
            if (a == null || !a.isCollisionEnabled()) continue;

            for (int j = i + 1; j < collidables.size(); j++) {
                CollidableEntity b = collidables.get(j);
                if (b == null || !b.isCollisionEnabled()) continue;

                String key = makePairKey(a.getId(), b.getId());
                if (processedPairs.contains(key)) continue;

                if (isColliding(a, b)) {
                    // Trigger collision callbacks
                    a.onCollision(b);
                    b.onCollision(a);
                    processedPairs.add(key);
                }
            }
        }

        // Remove entities with collision disabled (destroyed entities)
        removeDisabledEntities();
    }

    /**
     * Get an unmodifiable list of all collidables
     */
    public List<CollidableEntity> getCollidables() {
        return Collections.unmodifiableList(collidables);
    }

    /**
     * Get the count of collidable entities
     */
    public int getCount() {
        return collidables.size();
    }

    /**
     * Clear all collidables and processed pairs
     */
    public void clear() {
        collidables.clear();
        processedPairs.clear();
    }

    // -------------------------
    // Helpers (private)
    // -------------------------

    /**
     * Check if two entities are colliding using circle collision detection
     */
    private boolean isColliding(CollidableEntity a, CollidableEntity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double r = a.getCollisionRadius() + b.getCollisionRadius();
        double dist2 = dx * dx + dy * dy;

        return dist2 <= (r * r);
    }

    /**
     * Create a unique key for a pair of entities
     */
    private String makePairKey(int idA, int idB) {
        int lo = Math.min(idA, idB);
        int hi = Math.max(idA, idB);
        return lo + ":" + hi;
    }

    /**
     * Remove entities that have collision disabled (destroyed)
     */
    private void removeDisabledEntities() {
        Iterator<CollidableEntity> iterator = collidables.iterator();
        while (iterator.hasNext()) {
            CollidableEntity entity = iterator.next();
            if (entity != null && !entity.isCollisionEnabled()) {
                iterator.remove();
            }
        }
    }
}