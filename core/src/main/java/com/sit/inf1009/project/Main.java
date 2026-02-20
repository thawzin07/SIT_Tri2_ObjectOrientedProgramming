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
        batch = new SpriteBatch();
        font = new BitmapFont();

        // IO wiring
        new KeyboardInputHandler(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler()); // enables SOUND_PLAY
        
        // Populate initial scene
        loadEntitiesForLevel(1);
        
    }
    
    // helper method to instantiate and register entities based on current scene
    private void loadEntitiesForLevel(int levelNum) {
    	//create player entity
        Entity player = new Entity(1); // 1 = entity ID (can be used for anything, here just a unique identifier)
        player.setXPosition(200);
        player.setYPosition(200);
        
        //attach components: input-driven movement and physical collision bounds
        player.setMovement(new PlayerMovement(ioManager, 250f));
        player.setCollidable(new CollidableComponent(15, true));
        
        //delegate registration to scenemanager
        sceneManager.spawnEntity(player);

        player.setMovement(new PlayerMovement(ioManager, 250));
        player.setCollidable(new CollidableComponent(15, true)); // radius 15
        player.getCollidable().setRemoveOnCollision(false);


        // create NPC entities
        java.util.Random rng = new java.util.Random();
        int npcCount = (levelNum == 1) ? 8 : 4; 

        for (int i = 0; i < npcCount; i++) {
            Entity npc = new Entity(100 + i);
            npc.setXPosition(100 + rng.nextInt(500));
            npc.setYPosition(100 + rng.nextInt(300));
            
            //randomize starting movement directions
            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;

            npc.setMovement(new AIMovement(120, dirX, dirY));
            npc.setCollidable(new CollidableComponent(8, true));
            
            sceneManager.spawnEntity(npc);
        }
    }

    @Override
    public void render() {
    	//clear background completely before drawing the next frame
        ScreenUtils.clear(0, 0, 0, 1);

        // calculate time passed since last frame
        double dt = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
            sceneManager.push(new Scene("Level 3", Color.TEAL));
            loadEntitiesForLevel(3); 
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            sceneManager.push(new Scene("Level 2", Color.MAROON));
            loadEntitiesForLevel(2); 
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
            loadEntitiesForLevel(1); 
        }
        
        // 1) Move
        movementManager.updateAll(dt);
        
        // 2) Update current scene state timers and clamping (e.g. keep entities on screen)
        sceneManager.update((float) dt);   

        // 3) Collisions (queues deletions + plays clink)
        collisionManager.update();

       
        // 4) Apply deletions (entities disappear)
        entityManager.flushRemovals();
        
        // 5) Apply current scene background color
        sceneManager.render(null);

        // 6) Draw (simple: draw everyone as circles using collidable radius)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : entityManager.getEntities()) {
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;

            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }

        shapeRenderer.end();
        
        batch.begin();
        font.draw(batch, "Move with WASD", 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Switch between scenes with num 1, 2 & 3", 20, Gdx.graphics.getHeight() - 5);
        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown(); // optional but nice cleanup
    }
}
