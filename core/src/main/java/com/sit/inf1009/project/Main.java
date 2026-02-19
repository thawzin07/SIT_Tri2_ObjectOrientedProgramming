package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;

import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.PlayerMovement;

import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;
import com.sit.inf1009.project.engine.core.handlers.SoundOutputHandler;

import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;

import java.util.Random;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shapeRenderer;

    private InputOutputManager ioManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        // Managers
        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager(entityManager, movementManager, collisionManager);
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));

        // IO wiring
        new KeyboardInputHandler(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler()); // enables SOUND_PLAY

        // --- Create a PLAYER (just an Entity) ---
        Entity player = new Entity(1);
        player.setXPosition(200);
        player.setYPosition(200);
        player.setMovement(new PlayerMovement(ioManager, 250));
        player.setCollidable(new CollidableComponent(15, true)); // radius 15

        entityManager.addEntity(player);
        movementManager.addMovable(player);
        
        sceneManager.spawnEntity(player);

        // --- Create AI balls (just Entities) ---
        Random rng = new Random();
        for (int i = 0; i < 8; i++) {
            Entity npc = new Entity(100 + i);
            npc.setXPosition(100 + rng.nextInt(500));
            npc.setYPosition(100 + rng.nextInt(300));

            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;

            npc.setMovement(new AIMovement(120, dirX, dirY));
            npc.setCollidable(new CollidableComponent(8, true)); // radius 8

//            entityManager.addEntity(npc);
//            movementManager.addMovable(npc);
            sceneManager.spawnEntity(npc);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        double dt = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            sceneManager.push(new Scene("Level 2", Color.MAROON));
        }
        
        // Press '1' to pop back to the previous scene (Level 1)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            sceneManager.pop();
        }
        
        // 1) Move
        movementManager.updateAll(dt);

        // 2) Collisions (queues deletions + plays clink)
        collisionManager.update();

        
        sceneManager.update((float) dt);
        
        // 3) Apply deletions (entities disappear)
        entityManager.flushRemovals();
        
        sceneManager.render(null);

        // 4) Draw (simple: draw everyone as circles using collidable radius)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : entityManager.getEntities()) {
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;

            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        ioManager.shutdown(); // optional but nice cleanup
    }
}
