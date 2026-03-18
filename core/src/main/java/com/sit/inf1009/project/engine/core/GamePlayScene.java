package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.entities.Entity;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.sit.inf1009.project.engine.entities.PlateEntity;
import com.sit.inf1009.project.engine.entities.FoodFactory;
import com.sit.inf1009.project.engine.entities.FoodEntity;
import com.sit.inf1009.project.engine.components.PlayerMovement;
import com.sit.inf1009.project.engine.components.PlayerCollidableComponent;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;

// Temp placement, replace this later
import com.sit.inf1009.project.engine.components.CollidableComponent;

public class GamePlayScene extends Scene {

	
	// Old placeholder
//    private BitmapFont font;
//
//    public GamePlayScene() {
//        super("Gameplay", Color.BLACK);
//    }
//
//    @Override
//    public void create() {
//        font = new BitmapFont();
//        font.getData().setScale(2f);
//        // TODO: load your game assets, entities, etc. here
//    }
//
//    @Override
//    public void update(float dt, List<Entity> entities) {
//        super.update(dt, entities); // handles clamping + timeAlive
//        // TODO: add your gameplay update logic here
//    }
//
//    @Override
//    public void render(SpriteBatch batch) {
//        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//        font.setColor(Color.WHITE);
//        font.draw(batch, "GAMEPLAY - Coming Soon!", 
//                  Gdx.graphics.getWidth() / 2f - 180,
//                  Gdx.graphics.getHeight() / 2f);
//        batch.end();
//        // TODO: render your entities, HUD, etc.
//    }
//

	
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private BitmapFont font;
    private EntityManager em;
    private MovementManager mm;
    private InputOutputManager io;
    private boolean worldSpawned = false;

    public GamePlayScene() {
        super("Gameplay", new Color(0.1f, 0.2f, 0.3f, 1f));
    }
    
    public void initManagers(EntityManager em, MovementManager mm, InputOutputManager io) {
        this.em = em;
        this.mm = mm;
        this.io = io;
    }


	@Override
	public void create() {
	    font = new BitmapFont();
	    font.getData().setScale(2f);

	    // Spawn only once
	    if (worldSpawned) return;
	    worldSpawned = true;

	    if (em == null || mm == null || io == null) {
	        throw new IllegalStateException("GamePlayScene managers not wired. Call initManagers(...) before create().");
	    }

	    // Plate
	    PlateEntity plate = new PlateEntity(1, 200, 200);
	    plate.setMovement(new PlayerMovement(io, 250f));
	    plate.setCollidable(new PlayerCollidableComponent(15));

	    em.addEntity(plate);
	    mm.addMovable(plate);

	    // Foods
	    FoodFactory foodFactory = new FoodFactory(1000, 8f, 120);
	    for (int i = 0; i < 5; i++) {
	        FoodEntity a = foodFactory.getFood(FoodCategory.VEGETABLE);
	        FoodEntity b = foodFactory.getFood(FoodCategory.PROTEIN);
	        FoodEntity c = foodFactory.getFood(FoodCategory.CARBOHYDRATE);
	        FoodEntity d = foodFactory.getFood(FoodCategory.OIL);

	        em.addEntity(a); mm.addMovable(a);
	        em.addEntity(b); mm.addMovable(b);
	        em.addEntity(c); mm.addMovable(c);
	        em.addEntity(d); mm.addMovable(d);
	    }
	}
	
    public void render(SpriteBatch batch) {
        // Clear background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Render entities as colored circles ---
        shapeRenderer.begin(ShapeType.Filled);

        if (em != null) {
            for (Entity e : em.getEntities()) {

                // Set colour (FoodEntity uses its own colour; others default to white)
                if (e instanceof FoodEntity food) {
                    shapeRenderer.setColor(food.getColor());
                } else {
                    shapeRenderer.setColor(Color.WHITE);
                }

                
                CollidableComponent c = e.getCollidable();
                float r = (c != null) ? (float) c.getCollisionRadius() : 6f;

                shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
            }
        }

        shapeRenderer.end();

        // --- HUD / text ---
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "GAMEPLAY", 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }
	//  @Override
	  public void dispose() {
	      if (font != null) font.dispose();
	  }
}