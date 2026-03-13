package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.PlayerMovement;

import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;
import com.sit.inf1009.project.engine.core.handlers.LibGdxMouseInputHandler;
import com.sit.inf1009.project.engine.core.handlers.PlayerImageInputService;
import com.sit.inf1009.project.engine.core.handlers.SoundOutputHandler;

import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private InputOutputManager ioManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    private boolean paused;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        // Managers
        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        
        // --- PROFESSOR'S FEEDBACK: SceneManager is now simple ---
        sceneManager = new SceneManager(); 
        
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
        batch = new SpriteBatch();
        font = new BitmapFont();
        paused = false;

        // IO wiring
        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
        new PlayerImageInputService(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler()); 
        
        // Populate initial scene
        loadEntitiesForLevel(1);
    }
    
    private void loadEntitiesForLevel(int levelNum) {
        // Create player entity
        Entity player = new Entity(1);
        player.setXPosition(200);
        player.setYPosition(200);
        player.setMovement(new PlayerMovement(ioManager, 250f));

        CollidableComponent pc = new CollidableComponent(15, true);
        pc.setRemoveOnCollision(false);
        player.setCollidable(pc);

        // --- GAME MASTER ROUTING ---
        // Instead of sceneManager.spawnEntity, Main routes to both managers directly
        entityManager.addEntity(player);
        movementManager.addMovable(player);

        // Create NPC entities
        java.util.Random rng = new java.util.Random();
        int npcCount = (levelNum == 1) ? 8 : 4; 

        for (int i = 0; i < npcCount; i++) {
            Entity npc = new Entity(100 + i);
            npc.setXPosition(100 + rng.nextInt(500));
            npc.setYPosition(100 + rng.nextInt(300));
            
            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;

            npc.setMovement(new AIMovement(120, dirX, dirY));
            npc.setCollidable(new CollidableComponent(8, true));
            
            // --- GAME MASTER ROUTING ---
            entityManager.addEntity(npc);
            movementManager.addMovable(npc);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        double dt = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            paused = !paused;
        }

        // --- SCENE SWITCHING LOGIC (With cleanup) ---
        if (isSceneKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3, com.badlogic.gdx.Input.Keys.NUMPAD_3)) {
            prepareNewScene();
            sceneManager.push(new Scene("Level 3", Color.TEAL));
            loadEntitiesForLevel(3); 
        }

        if (isSceneKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2, com.badlogic.gdx.Input.Keys.NUMPAD_2)) {
            prepareNewScene();
            sceneManager.push(new Scene("Level 2", Color.MAROON));
            loadEntitiesForLevel(2); 
        }

        if (isSceneKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1, com.badlogic.gdx.Input.Keys.NUMPAD_1)) {
            prepareNewScene();
            sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
            loadEntitiesForLevel(1); 
        }
        
        if (!paused) {
            // 1) Move
            movementManager.updateAll(dt);

            // 2) Update Scene Boundaries
            // Main hands the entity list to SceneManager to process clamping
            sceneManager.update((float) dt, entityManager.getEntities());

            // 3) Collisions
            collisionManager.update();

            // 4) Apply deletions
            entityManager.flushRemovals();
        }
        
        // 5) Apply current scene background
        sceneManager.render(null);

        // 6) Draw
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getEntities()) {
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }
        shapeRenderer.end();
        
        // ... (UI drawing remains the same) ...
        renderUI();
    }

    // Helper to clear existing level data
    private void prepareNewScene() {
        entityManager.clear();
        movementManager.clear();
    }

    private void renderUI() {
        batch.begin();
        font.draw(batch, "Move with WASD", 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Switch between scenes with num 1, 2 & 3", 20, Gdx.graphics.getHeight() - 5);
        font.draw(batch, "Space: Pause/Resume", 20, Gdx.graphics.getHeight() - 35);
        if (paused) {
            font.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f - 25f, Gdx.graphics.getHeight() / 2f);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }

    private boolean isSceneKeyJustPressed(int mainKey, int numpadKey) {
        return Gdx.input.isKeyJustPressed(mainKey) || Gdx.input.isKeyJustPressed(numpadKey);
    }
}