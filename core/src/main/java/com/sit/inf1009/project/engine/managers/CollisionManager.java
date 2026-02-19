package com.sit.inf1009.project.engine.managers;
import java.util.ArrayList;
import java.util.List;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.entities.Entity;


public class CollisionManager {

    private final EntityManager entityManager;
    // Optional hook: keep if you already have it. Otherwise delete both lines.
    private final InputOutputManager ioManager;

    public CollisionManager(EntityManager entityManager, InputOutputManager ioManager) {
        if (entityManager == null) throw new IllegalArgumentException("EntityManager cannot be null");
        this.entityManager = entityManager;
        this.ioManager = ioManager;
    }

    public void update() {
        // 1) Gather collidable entities (snapshot list to avoid issues)
        List<Entity> collidableEntities = new ArrayList<>();
        for (Entity e : entityManager.getEntities()) {
            CollidableComponent c = e.getCollidable();
            if (c != null && c.isCollisionEnabled()) {
                collidableEntities.add(e);
            }
        }

        int n = collidableEntities.size();
        if (n < 2) return;

        // 2) Check each pair once
        for (int i = 0; i < n; i++) {
            Entity a = collidableEntities.get(i);
            CollidableComponent ca = a.getCollidable();
            if (ca == null || !ca.isCollisionEnabled()) continue;

            for (int j = i + 1; j < n; j++) {
                Entity b = collidableEntities.get(j);
                CollidableComponent cb = b.getCollidable();
                if (cb == null || !cb.isCollisionEnabled()) continue;

                if (isColliding(a, ca, b, cb)) {
                    // Optional: call component hooks (effects only, not deletions)
                    ca.onCollision(cb);
                    cb.onCollision(ca);
                    }
            }
        }
    }

    private boolean isColliding(Entity a, CollidableComponent ca, Entity b, CollidableComponent cb) {
        double dx = a.getXPosition() - b.getXPosition();
        double dy = a.getYPosition() - b.getYPosition();

        double r = ca.getCollisionRadius() + cb.getCollisionRadius();
        // Compare squared distance to avoid sqrt (faster + clean)
        return (dx * dx + dy * dy) <= (r * r);
    }

    // Debug helper
    public int getCount() {
        int count = 0;
        for (Entity e : entityManager.getEntities()) {
            if (e.getCollidable() != null) count++;
        }
        return count;
    }

    public void clear() {
        // Nothing stored internally right now.
        // Keeping method in case your UML expects it.
    }
}
