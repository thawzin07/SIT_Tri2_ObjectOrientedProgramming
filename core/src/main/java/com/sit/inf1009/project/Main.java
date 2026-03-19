package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Preferences;

import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.PlayerMovement;
import com.sit.inf1009.project.engine.core.handlers.*;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.*;

import java.util.List;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private InputOutputManager ioManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    
    // --- Game State Variables ---
    private boolean paused = false;
    private String playerName = "";
    private boolean isTypingName = false;
    private float gameTimer = 60f;
    private int score = 0;

    // Food Counts
    public int vegCount = 0, proteinCount = 0, carbCount = 0, oilCount = 0;

    // Leaderboard Data
    private String[] topNames = new String[3];
    private int[] topScores = new int[3];

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager(); 

        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
        new PlayerImageInputService(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler()); 

        loadHighScores();
        sceneManager.push(new Scene("Start", Color.BLACK));
    }

    @Override
    public void render() {
        String state = sceneManager.getCurrentSceneName();
        float dt = Gdx.graphics.getDeltaTime();

        handleMenuInput(state);

        if (state.equals("Game") && !paused) {
            updateGameLogic(dt);
        }

        ScreenUtils.clear(0, 0, 0, 1);
        sceneManager.render(null); 

        if (state.equals("Game")) {
            drawEntities();
        }

        drawUI(state);
    }

    private void handleMenuInput(String state) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y
        boolean clicked = Gdx.input.justTouched();

        if (state.equals("Start")) {
            // Button area for "CLICK TO START"
            if (clicked && isInside(mx, my, 300, 280, 200, 50)) {
                isTypingName = true;
                playerName = ""; // Reset name for new entry
            }
            if (isTypingName) handleNameTyping();
        }

        else if (state.equals("Difficulty")) {
            // Easy Button
            if (clicked && isInside(mx, my, 300, 300, 200, 40)) setDifficulty(60f, 1.0f, Color.GREEN);
            // Hard Button
            if (clicked && isInside(mx, my, 300, 250, 200, 40)) setDifficulty(20f, 2.0f, Color.RED);
        }

        else if (state.equals("Instructions")) {
            if (clicked) startGame();
        }

        else if (state.equals("HighScore")) {
            if (clicked) sceneManager.push(new Scene("Start", Color.BLACK));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) paused = !paused;
    }

    private void handleNameTyping() {
        // Listen for keys A-Z (LibGDX constants 29-54)
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                playerName += Input.Keys.toString(i);
            }
        }
        // Handle Backspace
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
        // Finish typing
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !playerName.isEmpty()) {
            isTypingName = false;
            sceneManager.push(new Scene("Difficulty", Color.DARK_GRAY));
        }
    }

    private boolean isInside(float mx, float my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void setDifficulty(float time, float speed, Color color) {
        this.gameTimer = time;
        // In the future, this speed variable can be used by teammates
        sceneManager.push(new Scene("Instructions", color));
    }

    private void startGame() {
        prepareNewScene();
        score = 0;
        vegCount = 0; proteinCount = 0; carbCount = 0; oilCount = 0;
        sceneManager.push(new Scene("Game", Color.BLACK));
        loadEntitiesForLevel(1); 
    }

    private void updateGameLogic(float dt) {
        gameTimer -= dt;
        if (gameTimer <= 0) {
            saveHighScore();
            sceneManager.push(new Scene("HighScore", Color.GOLD));
            return;
        }

        // Keep debug keys for your testing
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) vegCount++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) proteinCount++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) carbCount++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) oilCount++;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            submitPlate();
        }

        movementManager.updateAll(dt);
        sceneManager.update(dt, entityManager.getEntities());
        collisionManager.update();
        entityManager.flushRemovals();
    }

    private void submitPlate() {
        if (vegCount >= 2 && vegCount <= 4 && proteinCount >= 1 && proteinCount <= 3 
            && carbCount >= 1 && carbCount <= 2 && oilCount <= 1) {
            score += 150;
            gameTimer += 10;
        } else {
            gameTimer -= 10;
        }
        vegCount = 0; proteinCount = 0; carbCount = 0; oilCount = 0;
    }

    private void drawEntities() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getEntities()) {
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }
        shapeRenderer.end();
    }

    private void drawUI(String state) {
        batch.begin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (state.equals("Start")) {
            font.draw(batch, "HEALTHY PLATE COLLECTOR", w/2 - 80, h/2 + 100);
            if (isTypingName) {
                font.draw(batch, "ENTER NAME: " + playerName + "_", w/2 - 80, h/2 + 20);
                font.draw(batch, "(Press ENTER to confirm)", w/2 - 70, h/2 - 20);
            } else {
                font.draw(batch, "[ CLICK HERE TO START ]", 300, 300);
            }
        } else if (state.equals("Difficulty")) {
            font.draw(batch, "Welcome " + playerName, 300, 400);
            font.draw(batch, "[ CLICK EASY MODE ]", 300, 320);
            font.draw(batch, "[ CLICK HARD MODE ]", 300, 270);
        } else if (state.equals("Instructions")) {
            font.draw(batch, "GOAL: Veg(2-4), Prot(1-3), Carb(1-2), Oil(0-1)", w/2 - 150, h/2 + 40);
            font.draw(batch, "CLICK ANYWHERE TO BEGIN", w/2 - 80, h/2 - 20);
        } else if (state.equals("Game")) {
            font.draw(batch, "Time: " + (int)gameTimer + "s | Score: " + score, 20, h - 20);
            font.draw(batch, "Veg: " + vegCount + " | Prot: " + proteinCount + " | Carb: " + carbCount + " | Oil: " + oilCount, 20, h - 40);
            font.draw(batch, "Press ENTER to submit", 20, 30);
        } else if (state.equals("HighScore")) {
            font.draw(batch, "LEADERBOARD", w/2 - 40, h/2 + 80);
            for (int i = 0; i < 3; i++) {
                font.draw(batch, (i+1) + ". " + topNames[i] + ": " + topScores[i], w/2 - 50, h/2 + 40 - (i*20));
            }
            font.draw(batch, "CLICK ANYWHERE TO RESTART", w/2 - 80, h/2 - 60);
        }
        batch.end();
    }

    private void saveHighScore() {
        Preferences prefs = Gdx.app.getPreferences("HealthyPlateScores");
        if (score > topScores[2]) {
            topScores[2] = score; topNames[2] = playerName;
            for (int i = 1; i >= 0; i--) {
                if (topScores[i+1] > topScores[i]) {
                    int ts = topScores[i]; topScores[i] = topScores[i+1]; topScores[i+1] = ts;
                    String tn = topNames[i]; topNames[i] = topNames[i+1]; topNames[i+1] = tn;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            prefs.putInteger("s"+i, topScores[i]);
            prefs.putString("n"+i, topNames[i]);
        }
        prefs.flush();
    }

    private void loadHighScores() {
        Preferences prefs = Gdx.app.getPreferences("HealthyPlateScores");
        for (int i = 0; i < 3; i++) {
            topScores[i] = prefs.getInteger("s"+i, 0);
            topNames[i] = prefs.getString("n"+i, "-");
        }
    }

    private void loadEntitiesForLevel(int levelNum) {
        Entity player = new Entity(1);
        player.setXPosition(400); player.setYPosition(50);
        player.setMovement(new PlayerMovement(ioManager, 300f));
        player.setCollidable(new CollidableComponent(20, true));
        entityManager.addEntity(player);
        movementManager.addMovable(player);
    }

    private void prepareNewScene() {
        entityManager.clear();
        movementManager.clear();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }
}