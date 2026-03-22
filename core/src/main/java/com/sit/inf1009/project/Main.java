package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.FoodCollidableComponent;
import com.sit.inf1009.project.engine.components.MovementComponent;
import com.sit.inf1009.project.engine.components.PlayerCollidableComponent;
import com.sit.inf1009.project.engine.components.PlayerMovement;
import com.sit.inf1009.project.engine.core.AvatarSetupFlowScreen;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.core.StartMenuScene;
import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;
import com.sit.inf1009.project.engine.core.handlers.LibGdxMouseInputHandler;
import com.sit.inf1009.project.engine.core.handlers.PlayerImageInputService;
import com.sit.inf1009.project.engine.core.handlers.SoundOutputHandler;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;
import com.sit.inf1009.project.engine.entities.FoodFactory;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main extends ApplicationAdapter {

    private enum GameState {
        FOOD_MENU,
        DIFFICULTY_SETTINGS,
        HOW_TO_PLAY,
        AVATAR_SETUP,
        PLAYING,
        LEADERBOARD_ENTRY,
        LEADERBOARD_VIEW
    }

    private enum DifficultyPreset {
        EASY("Easy", 75f, 6, 95f, 8, 6f, 3f, 10),
        NORMAL("Normal", 60f, 8, 120f, 10, 5f, 5f, 15),
        HARD("Hard", 45f, 10, 150f, 12, 4f, 6f, 20);

        final String label;
        final float startingTimer;
        final int npcCount;
        final float npcSpeed;
        final int healthyScoreBonus;
        final float healthyTimerBonus;
        final float unhealthyTimerPenalty;
        final int foodEntityCount;

        DifficultyPreset(String label,
                         float startingTimer,
                         int npcCount,
                         float npcSpeed,
                         int healthyScoreBonus,
                         float healthyTimerBonus,
                         float unhealthyTimerPenalty, 
                         int foodEntityCount) {
            this.label = label;
            this.startingTimer = startingTimer;
            this.npcCount = npcCount;
            this.npcSpeed = npcSpeed;
            this.healthyScoreBonus = healthyScoreBonus;
            this.healthyTimerBonus = healthyTimerBonus;
            this.unhealthyTimerPenalty = unhealthyTimerPenalty;
            this.foodEntityCount = foodEntityCount;
        }
    }

    private static class LeaderboardEntry {
        final String name;
        final int score;
        final Texture avatarTexture;
        final boolean ownsTexture;
        final int presetIndex;
        final String uploadedPath;

        LeaderboardEntry(String name,
                         int score,
                         Texture avatarTexture,
                         boolean ownsTexture,
                         int presetIndex,
                         String uploadedPath) {
            this.name = name;
            this.score = score;
            this.avatarTexture = avatarTexture;
            this.ownsTexture = ownsTexture;
            this.presetIndex = presetIndex;
            this.uploadedPath = uploadedPath;
        }
    }

    private static final int PLAYER_ID = 1;
    private static final float BUTTON_W = 250f;
    private static final float BUTTON_H = 38f;
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private final LeaderboardFileStore leaderboardFileStore = new LeaderboardFileStore();

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private StartMenuScene foodMenuScene;
    private AvatarSetupFlowScreen avatarSetupScreen;
    private OrthographicCamera camera;
    private Vector3 touchPos = new Vector3();

    private InputOutputManager ioManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    private GameSession gameSession;
    private PlayerImageInputService playerImageInputService;

    private GameState gameState;
    private DifficultyPreset difficultyPreset;
    private DifficultyConfig difficultyConfig;
    private boolean paused;
    private boolean rulesOpenedFromPause = false;

    private Texture[] presetAvatars;
    private String[] presetAvatarLabels;
    private Map<FoodCategory, Texture> foodCategoryTextures;
    private Texture uploadedAvatarTexture;
    private String uploadedAvatarPath;
    private Texture selectedAvatarTexture;
    private boolean selectedAvatarIsUploaded;
    private int selectedPresetIndex;

    private final List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    private String playerNameInput = "";
    private String statusMessage = "";
    private float statusSecondsLeft = 0f;
    private boolean leaderboardOpenedFromMenu;

    private boolean clickPending;
    private float clickX;
    private float clickY;
    private boolean leaderboardNameEditing;
    private int referenceViewportWidth;
    private int referenceViewportHeight;
    private double currentGameplayScale = 1.0;
    private int lastViewportWidth;
    private int lastViewportHeight;
    
    private static final int foodId = 100;
    private int nextFoodId = foodId;
    private FoodFactory foodFactory;
    private FoodSpawnCoordinator foodSpawnCoordinator;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager();
        foodSpawnCoordinator = new FoodSpawnCoordinator(entityManager, movementManager);
        difficultyPreset = DifficultyPreset.NORMAL;
        difficultyConfig = toDifficultyConfig(difficultyPreset);
        gameSession = new GameSession(
                difficultyConfig.getStartingTimer(),
                difficultyConfig.getHealthyScoreBonus(),
                difficultyConfig.getHealthyTimerBonus(),
                difficultyConfig.getUnhealthyTimerPenalty());
        paused = false;

        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
        playerImageInputService = new PlayerImageInputService(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler());

        presetAvatarLabels = new String[] { "Droplet", "Bucket", "LibGDX" };
        presetAvatars = new Texture[] {
                new Texture(Gdx.files.internal("droplet.png")),
                new Texture(Gdx.files.internal("bucket.png")),
                new Texture(Gdx.files.internal("libgdx.png"))
        };
        foodCategoryTextures = createFoodCategoryTextures();
        selectedPresetIndex = 0;
        selectedAvatarTexture = presetAvatars[selectedPresetIndex];
        selectedAvatarIsUploaded = false;
        loadLeaderboardEntries();

        foodMenuScene = new StartMenuScene(ioManager, new StartMenuScene.ActionListener() {
            @Override
            public void onStart() {
                gameState = GameState.AVATAR_SETUP;
            }

            @Override
            public void onDifficulty() {
                gameState = GameState.DIFFICULTY_SETTINGS;
            }

            @Override
            public void onHowToPlay() {
                gameState = GameState.HOW_TO_PLAY;
            }

            @Override
            public void onHighScores() {
                leaderboardOpenedFromMenu = true;
                gameState = GameState.LEADERBOARD_VIEW;
            }
        });
        foodMenuScene.create();
        foodMenuScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        avatarSetupScreen = new AvatarSetupFlowScreen(ioManager, presetAvatars, presetAvatarLabels, new AvatarSetupFlowScreen.ActionListener() {
            @Override
            public void onBackToMainMenu() {
                gameState = GameState.FOOD_MENU;
            }

            @Override
            public void onStartGame(AvatarSetupFlowScreen.SelectionResult result) {
                applyAvatarSelection(result);
                startNewGame();
            }
        });
        avatarSetupScreen.create();
        avatarSetupScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        wirePlayerImageSelectionEvents();
        lastViewportWidth = Math.max(1, Gdx.graphics.getWidth());
        lastViewportHeight = Math.max(1, Gdx.graphics.getHeight());
        referenceViewportWidth = lastViewportWidth;
        referenceViewportHeight = lastViewportHeight;
        currentGameplayScale = 1.0;
        gameState = GameState.FOOD_MENU;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);

        float dt = Gdx.graphics.getDeltaTime();
        if (statusSecondsLeft > 0f) {
            statusSecondsLeft -= dt;
        }

        captureClick();

        switch (gameState) {
            case FOOD_MENU:
                foodMenuScene.render(batch);
                break;
            case DIFFICULTY_SETTINGS:
                renderDifficultySettings();
                break;
            case HOW_TO_PLAY:
                renderHowToPlay();
                break;
            case AVATAR_SETUP:
                avatarSetupScreen.render(batch);
                break;
            case PLAYING:
                renderGameplay(dt);
                break;
            case LEADERBOARD_ENTRY:
                renderLeaderboardEntry();
                break;
            case LEADERBOARD_VIEW:
                renderLeaderboardView();
                break;
            default:
                break;
        }
    }

    private void renderDifficultySettings() {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(760f, width - 80f);
        float panelH = Math.min(500f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        Rectangle easyButton = new Rectangle(panel.x + 40f, panel.y + panelH - 180f, panelW - 80f, 48f);
        Rectangle normalButton = new Rectangle(panel.x + 40f, panel.y + panelH - 240f, panelW - 80f, 48f);
        Rectangle hardButton = new Rectangle(panel.x + 40f, panel.y + panelH - 300f, panelW - 80f, 48f);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        if (consumeClick(easyButton)) {
            difficultyPreset = DifficultyPreset.EASY;
            difficultyConfig = toDifficultyConfig(difficultyPreset);
            showStatus("Difficulty set: Easy", 2f);
        }
        if (consumeClick(normalButton)) {
            difficultyPreset = DifficultyPreset.NORMAL;
            difficultyConfig = toDifficultyConfig(difficultyPreset);
            showStatus("Difficulty set: Normal", 2f);
        }
        if (consumeClick(hardButton)) {
            difficultyPreset = DifficultyPreset.HARD;
            difficultyConfig = toDifficultyConfig(difficultyPreset);
            showStatus("Difficulty set: Hard", 2f);
        }
        if (consumeClick(backButton)) {
            gameState = GameState.FOOD_MENU;
        }

        drawScreenPanel(panel);
        drawActionButton(easyButton, difficultyPreset == DifficultyPreset.EASY ? new Color(0.16f, 0.62f, 0.2f, 1f) : new Color(0.12f, 0.34f, 0.18f, 1f));
        drawActionButton(normalButton, difficultyPreset == DifficultyPreset.NORMAL ? new Color(0.1f, 0.45f, 0.78f, 1f) : new Color(0.1f, 0.26f, 0.45f, 1f));
        drawActionButton(hardButton, difficultyPreset == DifficultyPreset.HARD ? new Color(0.75f, 0.22f, 0.22f, 1f) : new Color(0.4f, 0.16f, 0.16f, 1f));
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        font.draw(batch, "GAME SETTINGS", panel.x + 40f, panel.y + panelH - 28f);
        font.draw(batch, "Difficulty: " + difficultyPreset.label, panel.x + 40f, panel.y + panelH - 58f);
        font.draw(batch, "Easy   - 75s, slower, +6s / -3s submit, 10 Food items", easyButton.x + 20f, easyButton.y + 30f);
        font.draw(batch, "Normal - 60s, balanced, +5s / -5s submit, 15 Food items", normalButton.x + 20f, normalButton.y + 30f);
        font.draw(batch, "Hard   - 45s, faster, +4s / -6s submit, 20 Food items", hardButton.x + 20f, hardButton.y + 30f);
        font.draw(batch, "Back to Main Menu", backButton.x + 20f, backButton.y + 28f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderHowToPlay() {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(840f, width - 80f);
        float panelH = Math.min(520f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        if (consumeClick(backButton)) {
            if (rulesOpenedFromPause) {
                gameState = GameState.PLAYING;
                rulesOpenedFromPause = false; // Reset it
            } else {
                gameState = GameState.FOOD_MENU;
            }
        }

        drawScreenPanel(panel);
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float textX = panel.x + 40f;
        float topY = panel.y + panelH - 28f;
        font.draw(batch, "HOW TO PLAY", textX, topY);
        font.draw(batch, "1. Press START on the main menu.", textX, topY - 42f);
        font.draw(batch, "2. Select a preset avatar or upload your own image.", textX, topY - 70f);
        font.draw(batch, "3. Press Start Game to begin.", textX, topY - 98f);
        font.draw(batch, "4. Move with WASD and catch food items.", textX, topY - 126f);
        font.draw(batch, "5. Build a healthy plate target: Veg: 2-4 Protein: 1-3 Carbs: 1-2 Oil: 0-1", textX, topY - 154f);
        font.draw(batch, "6. Press Enter to submit plate (this resets plate).", textX, topY - 182f);
        font.draw(batch, "7. Press R to clear plate, Space to pause/resume.", textX, topY - 210f);
        font.draw(batch, "8. Timer end -> submit name/avatar to leaderboard.", textX, topY - 238f);
        String backText = rulesOpenedFromPause ? "Back to Pause Menu" : "Back to Main Menu";
        font.draw(batch, backText, backButton.x + 20f, backButton.y + 28f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderGameplay(float dt) {
        applyFullScreenProjection();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            paused = !paused;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            submitPlate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetPlate();
            showStatus("Plate reset", 2f);
        }

        if (isSceneKeyJustPressed(Input.Keys.NUM_3, Input.Keys.NUMPAD_3)) {
            sceneManager.push(new Scene("Level 3", Color.TEAL));
            loadEntitiesForLevel(3);
        }
        if (isSceneKeyJustPressed(Input.Keys.NUM_2, Input.Keys.NUMPAD_2)) {
            sceneManager.push(new Scene("Level 2", Color.MAROON));
            loadEntitiesForLevel(2);
        }
        if (isSceneKeyJustPressed(Input.Keys.NUM_1, Input.Keys.NUMPAD_1)) {
            sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
            loadEntitiesForLevel(1);
        }

        if (!paused) {
            movementManager.updateAll(dt);
            sceneManager.update(dt, entityManager.getEntities());
            collisionManager.update();
            entityManager.flushRemovals();
            nextFoodId = foodSpawnCoordinator.ensureFoodDiversityAndCount(
                    foodFactory,
                    nextFoodId,
                    difficultyConfig.getFoodEntityCount());

            float nextTimer = Math.max(0f, gameSession.getTimer() - dt);
            gameSession.setTimer(nextTimer);
            if (nextTimer <= 0f) {
                paused = false;
                gameState = GameState.LEADERBOARD_ENTRY;
                leaderboardNameEditing = true;
                showStatus("Time up! Enter name and submit to leaderboard", 4f);
            }
        }

        sceneManager.render(null);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getEntities()) {
            if (e.getTexture() != null) continue;
            CollidableComponent c = e.getCollidable();
            float r = (c != null) ? (float) c.getCollisionRadius() : 6f;
            shapeRenderer.setColor(getEntityRenderColor(e));
            shapeRenderer.circle((float) e.getXPosition(), (float) e.getYPosition(), r);
        }
        shapeRenderer.end();

        batch.begin();
        for (Entity e : entityManager.getEntities()) {
            Texture texture = e.getTexture();
            if (texture == null) continue;

            CollidableComponent c = e.getCollidable();
            float size = (c != null) ? (float) c.getCollisionRadius() * 2f : 32f;
            float x = (float) e.getXPosition() - (size / 2f);
            float y = (float) e.getYPosition() - (size / 2f);
            batch.draw(texture, x, y, size, size);
        }

        font.draw(batch, "Timer: " + (int) Math.ceil(gameSession.getTimer()), 20f, Gdx.graphics.getHeight() - 20f);
        font.draw(batch, "Score: " + gameSession.getScore(), 20f, Gdx.graphics.getHeight() - 38f);
        font.draw(batch, "Difficulty: " + difficultyPreset.label, 20f, Gdx.graphics.getHeight() - 56f);
        font.draw(batch, "Plate V/P/C/O: " + gameSession.getVegetableCount() + "/"
                + gameSession.getProteinCount() + "/" + gameSession.getCarbCount() + "/"
                + gameSession.getOilCount(), 20f, Gdx.graphics.getHeight() - 74f);

        drawStatus(batch, 20f, 24f);
        batch.end();

        if (paused) {
            float width = Gdx.graphics.getWidth();
            float height = Gdx.graphics.getHeight();
            float panelW = 350f;
            float panelH = 340f;
            float centerX = width / 2f;
            Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

            Rectangle resumeBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 120f, panelW - 80f, 44f);
            Rectangle restartBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 180f, panelW - 80f, 44f);
            Rectangle rulesBtn = new Rectangle(panel.x + 40f, panel.y + panelH - 240f, panelW - 80f, 44f);
            Rectangle quitBtn = new Rectangle(panel.x + 40f, panel.y + 40f, panelW - 80f, 44f);

            if (consumeClick(resumeBtn)) paused = false;
            if (consumeClick(restartBtn)) startNewGame();
            if (consumeClick(rulesBtn)) {
                rulesOpenedFromPause = true;
                gameState = GameState.HOW_TO_PLAY;
            }
            if (consumeClick(quitBtn)) {
                gameState = GameState.FOOD_MENU;
                paused = false;
            }

            drawScreenPanel(panel);
            drawActionButton(resumeBtn, new Color(0.16f, 0.62f, 0.2f, 1f));  // Green
            drawActionButton(restartBtn, new Color(0.1f, 0.45f, 0.78f, 1f)); // Blue
            drawActionButton(rulesBtn, new Color(0.6f, 0.4f, 0.1f, 1f));     // Orange
            drawActionButton(quitBtn, new Color(0.75f, 0.22f, 0.22f, 1f));   // Red

            batch.begin();
            font.draw(batch, "GAME PAUSED", panel.x + panelW / 2f - 45f, panel.y + panelH - 30f);
            font.draw(batch, "Resume Game", resumeBtn.x + 20f, resumeBtn.y + 28f);
            font.draw(batch, "Restart Game", restartBtn.x + 20f, restartBtn.y + 28f);
            font.draw(batch, "How to Play", rulesBtn.x + 20f, rulesBtn.y + 28f);
            font.draw(batch, "Quit to Main Menu", quitBtn.x + 20f, quitBtn.y + 28f);
            batch.end();
        }
    }

    private void renderLeaderboardEntry() {
        applyFullScreenProjection();
        handleLeaderboardNameTyping();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(760f, width - 80f);
        float panelH = Math.min(520f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);

        Rectangle nameField = new Rectangle(panel.x + 40f, panel.y + panelH - 170f, panelW - 80f, 48f);
        Rectangle uploadButton = new Rectangle(panel.x + 40f, panel.y + panelH - 235f, panelW - 80f, 48f);
        Rectangle submitButton = new Rectangle(panel.x + 40f, panel.y + panelH - 300f, panelW - 80f, 48f);
        Rectangle backButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);

        if (consumeClick(nameField)) {
            leaderboardNameEditing = true;
            showStatus("Typing enabled. Press Enter to confirm name.", 2.5f);
        }
        if (consumeClick(uploadButton)) {
            leaderboardNameEditing = false;
            requestImageUpload("leaderboard-entry");
        }
        if (consumeClick(submitButton)) {
            leaderboardNameEditing = false;
            submitLeaderboardEntry();
        }
        if (consumeClick(backButton)) {
            leaderboardNameEditing = false;
            gameState = GameState.FOOD_MENU;
        }

        drawScreenPanel(panel);
        drawTextInputField(nameField, leaderboardNameEditing);
        drawActionButton(uploadButton, new Color(0.12f, 0.34f, 0.5f, 1f));
        drawActionButton(submitButton, new Color(0.13f, 0.47f, 0.2f, 1f));
        drawActionButton(backButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float headerY = panel.y + panelH - 28f;
        font.draw(batch, "RUN COMPLETE", panel.x + 40f, headerY);
        font.draw(batch, "Final Score: " + gameSession.getScore(), panel.x + 40f, headerY - 30f);
        font.draw(batch, "Enter your name and submit to leaderboard", panel.x + 40f, headerY - 58f);

        if (selectedAvatarTexture != null) {
            font.draw(batch, "Avatar:", panel.x + 40f, headerY - 86f);
            batch.draw(selectedAvatarTexture, panel.x + 90f, headerY - 104f, 28f, 28f);
        } else {
            font.draw(batch, "Avatar: <none>", panel.x + 40f, headerY - 86f);
        }

        String shownName = playerNameInput.isBlank() ? "Type your name..." : playerNameInput;
        if (leaderboardNameEditing && ((System.currentTimeMillis() / 350L) % 2L == 0L)) {
            shownName += "_";
        }
        font.draw(batch, shownName, nameField.x + 16f, nameField.y + 30f);
        font.draw(batch, "Upload / Change Image", uploadButton.x + 16f, uploadButton.y + 30f);
        font.draw(batch, "Submit to Leaderboard", submitButton.x + 16f, submitButton.y + 30f);
        font.draw(batch, "Back to Main Menu", backButton.x + 16f, backButton.y + 28f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderLeaderboardView() {
        applyFullScreenProjection();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width / 2f;
        float panelW = Math.min(800f, width - 80f);
        float panelH = Math.min(540f, height - 80f);
        Rectangle panel = new Rectangle(centerX - panelW / 2f, (height - panelH) / 2f, panelW, panelH);
        String footerLabel = leaderboardOpenedFromMenu ? "Back to Main Menu" : "Play Again";
        Rectangle footerButton = new Rectangle(panel.x + 40f, panel.y + 30f, panelW - 80f, 44f);
        if (consumeClick(footerButton)) {
            gameState = GameState.FOOD_MENU;
            playerNameInput = "";
            leaderboardNameEditing = false;
            leaderboardOpenedFromMenu = false;
            showStatus("Setup ready for next run", 2f);
        }

        drawScreenPanel(panel);
        drawActionButton(footerButton, new Color(0.2f, 0.2f, 0.25f, 1f));

        batch.begin();
        float topY = panel.y + panelH - 28f;
        font.draw(batch, "LEADERBOARD", panel.x + 40f, topY);

        int maxRows = Math.min(10, leaderboardEntries.size());
        float rowY = topY - 42f;
        for (int i = 0; i < maxRows; i++) {
            LeaderboardEntry entry = leaderboardEntries.get(i);
            float rowX = panel.x + 40f;
            font.draw(batch, String.format("%2d.", i + 1), rowX, rowY);
            if (entry.avatarTexture != null) {
                batch.draw(entry.avatarTexture, rowX + 34f, rowY - 18f, 24f, 24f);
            }
            font.draw(batch, entry.name, rowX + 70f, rowY);
            font.draw(batch, "Score: " + entry.score, panel.x + panelW - 170f, rowY);
            rowY -= 30f;
        }

        if (leaderboardEntries.isEmpty()) {
            font.draw(batch, "No entries yet.", panel.x + 40f, topY - 42f);
        }

        font.draw(batch, footerLabel, footerButton.x + 16f, footerButton.y + 28f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void startNewGame() {
        gameSession = new GameSession(
                difficultyConfig.getStartingTimer(),
                difficultyConfig.getHealthyScoreBonus(),
                difficultyConfig.getHealthyTimerBonus(),
                difficultyConfig.getUnhealthyTimerPenalty());
        paused = false;
        leaderboardNameEditing = false;
        playerNameInput = "";
        entityManager.clear();
        movementManager.clear();
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
        loadEntitiesForLevel(1);
        gameState = GameState.PLAYING;
        showStatus("Game started (" + difficultyPreset.label + ")", 2f);
    }

    private void loadEntitiesForLevel(int levelNum) {
        double playerRadius = 15d * currentGameplayScale;
        double foodRadius = 8d * currentGameplayScale;
        double playerSpeed = 250d * currentGameplayScale;
        double npcSpeed = difficultyConfig.getNpcSpeed() * currentGameplayScale;
        int worldW = Math.max(1, Gdx.graphics.getWidth());
        int worldH = Math.max(1, Gdx.graphics.getHeight());

        Entity player = new Entity(PLAYER_ID);
        player.setXPosition(worldW * 0.3);
        player.setYPosition(worldH * 0.35);
        player.setMovement(new PlayerMovement(ioManager, playerSpeed));
        player.setCollidable(new PlayerCollidableComponent(playerRadius));
        if (selectedAvatarTexture != null) {
            player.setTexture(selectedAvatarTexture);
        }
        entityManager.addEntity(player);
        movementManager.addMovable(player);

        java.util.Random rng = new java.util.Random();
        int npcCount = (levelNum == 1) ? difficultyConfig.getNpcCount() : Math.max(4, difficultyConfig.getNpcCount() / 2);
        int minX = (int) Math.max(40, foodRadius * 2);
        int maxX = Math.max(minX + 1, worldW - minX);
        int minY = (int) Math.max(60, foodRadius * 2);
        int maxY = Math.max(minY + 1, worldH - minY);
        this.foodFactory = new FoodFactory(
                rng,
                foodRadius,
                npcSpeed,
                minX, maxX,
                minY, maxY,
                gameSession,
                foodCategoryTextures
        );

        nextFoodId = foodId;
        nextFoodId = foodSpawnCoordinator.spawnStartingFoods(
                this.foodFactory,
                nextFoodId,
                difficultyConfig.getFoodEntityCount());
    }

    private Map<FoodCategory, Texture> createFoodCategoryTextures() {
        Map<FoodCategory, Texture> textures = new EnumMap<>(FoodCategory.class);
        textures.put(FoodCategory.VEGETABLE, createFoodTexture(new Color(0.2f, 0.85f, 0.2f, 1f), new Color(0.08f, 0.45f, 0.08f, 1f)));
        textures.put(FoodCategory.PROTEIN, createFoodTexture(new Color(0.9f, 0.25f, 0.25f, 1f), new Color(0.5f, 0.12f, 0.12f, 1f)));
        textures.put(FoodCategory.CARBOHYDRATE, createFoodTexture(new Color(0.95f, 0.8f, 0.2f, 1f), new Color(0.65f, 0.5f, 0.05f, 1f)));
        textures.put(FoodCategory.OIL, createFoodTexture(new Color(0.72f, 0.42f, 0.9f, 1f), new Color(0.35f, 0.22f, 0.5f, 1f)));
        return textures;
    }

    private Texture createFoodTexture(Color fill, Color accent) {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        pixmap.setColor(fill);
        pixmap.fillCircle(32, 32, 28);

        pixmap.setColor(accent);
        pixmap.fillCircle(24, 24, 9);
        pixmap.fillCircle(40, 40, 7);

        pixmap.setColor(1f, 1f, 1f, 0.55f);
        pixmap.fillCircle(20, 43, 5);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void applyAvatarSelection(AvatarSetupFlowScreen.SelectionResult result) {
        if (result == null) return;

        if (result.isUploaded()) {
            try {
                Texture newTexture = new Texture(Gdx.files.absolute(result.getUploadedPath()));
                if (uploadedAvatarTexture != null) {
                    uploadedAvatarTexture.dispose();
                }
                uploadedAvatarTexture = newTexture;
                uploadedAvatarPath = result.getUploadedPath();
                selectedAvatarTexture = uploadedAvatarTexture;
                selectedAvatarIsUploaded = true;
                selectedPresetIndex = -1;
            } catch (Exception e) {
                showStatus("Image load failed", 3f);
            }
            return;
        }

        selectPresetAvatar(result.getPresetIndex());
    }

    private void selectPresetAvatar(int index) {
        if (index < 0 || index >= presetAvatars.length) return;
        selectedAvatarIsUploaded = false;
        selectedAvatarTexture = presetAvatars[index];
        selectedPresetIndex = index;
        applyAvatarToPlayer();
        showStatus("Preset selected: " + presetAvatarLabels[index], 2f);
    }

    private void requestImageUpload(String sourceTag) {
        ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, sourceTag));
    }

    private void handleLeaderboardNameTyping() {
        if (gameState != GameState.LEADERBOARD_ENTRY || !leaderboardNameEditing) {
            return;
        }

        LeaderboardNameEditor.Result result = LeaderboardNameEditor.update(playerNameInput, 24);
        playerNameInput = result.getUpdatedName();

        if (result.isConfirmed()) {
            leaderboardNameEditing = false;
            showStatus("Name confirmed", 2f);
            return;
        }
        if (result.isCanceled()) {
            leaderboardNameEditing = false;
            showStatus("Name edit cancelled", 2f);
        }
    }

    private void submitLeaderboardEntry() {
        if (playerNameInput == null || playerNameInput.isBlank()) {
            showStatus("Please enter your name", 3f);
            return;
        }
        if (selectedAvatarTexture == null) {
            showStatus("Please choose/upload an avatar image", 3f);
            return;
        }

        Texture entryTexture = selectedAvatarTexture;
        boolean ownsTexture = false;
        int entryPresetIndex = selectedAvatarIsUploaded ? -1 : selectedPresetIndex;
        String entryUploadedPath = selectedAvatarIsUploaded ? uploadedAvatarPath : null;
        if (selectedAvatarIsUploaded) {
            if (uploadedAvatarPath == null || uploadedAvatarPath.isBlank()) {
                showStatus("Uploaded avatar path missing", 3f);
                return;
            }
            try {
                entryTexture = new Texture(Gdx.files.absolute(uploadedAvatarPath));
                ownsTexture = true;
            } catch (Exception e) {
                showStatus("Failed to store uploaded avatar for leaderboard", 3f);
                return;
            }
        }

        leaderboardEntries.add(new LeaderboardEntry(
                sanitizeName(playerNameInput),
                gameSession.getScore(),
                entryTexture,
                ownsTexture,
                entryPresetIndex,
                entryUploadedPath));
        leaderboardEntries.sort(Comparator.comparingInt((LeaderboardEntry e) -> e.score).reversed());
        saveLeaderboardEntries();
        leaderboardOpenedFromMenu = false;
        gameState = GameState.LEADERBOARD_VIEW;
        showStatus("Leaderboard entry submitted", 2f);
    }

    private void loadLeaderboardEntries() {
        leaderboardEntries.clear();
        List<LeaderboardRecord> records = leaderboardFileStore.load(LEADERBOARD_FILE);
        for (LeaderboardRecord record : records) {
            String name = record.getName();
            int score = record.getScore();
            int presetIndex = record.getPresetIndex();
            String uploadedPath = record.getUploadedPath();

            Texture texture = null;
            boolean ownsTexture = false;
            if (uploadedPath != null) {
                try {
                    texture = new Texture(Gdx.files.absolute(uploadedPath));
                    ownsTexture = true;
                } catch (Exception ignored) {
                    texture = null;
                    ownsTexture = false;
                }
            }

            if (texture == null && presetIndex >= 0 && presetIndex < presetAvatars.length) {
                texture = presetAvatars[presetIndex];
            }

            leaderboardEntries.add(new LeaderboardEntry(name, score, texture, ownsTexture, presetIndex, uploadedPath));
        }

        leaderboardEntries.sort(Comparator.comparingInt((LeaderboardEntry e) -> e.score).reversed());
    }

    private void saveLeaderboardEntries() {
        List<LeaderboardRecord> records = new ArrayList<>();
        for (LeaderboardEntry entry : leaderboardEntries) {
            records.add(new LeaderboardRecord(
                    sanitizeName(entry.name),
                    entry.score,
                    entry.presetIndex,
                    entry.uploadedPath));
        }
        leaderboardFileStore.save(LEADERBOARD_FILE, records);
    }

    private String sanitizeName(String input) {
        if (input == null) {
            return "Player";
        }
        String safe = input.replace('\t', ' ').replace('\n', ' ').trim();
        return safe.isBlank() ? "Player" : safe;
    }

    private void wirePlayerImageSelectionEvents() {
        ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTED, event -> {
            if (gameState != GameState.LEADERBOARD_ENTRY) {
                return;
            }
            String path = event.requirePayload(String.class);
            try {
                Texture newTexture = new Texture(Gdx.files.absolute(path));
                if (uploadedAvatarTexture != null) {
                    uploadedAvatarTexture.dispose();
                }
                uploadedAvatarTexture = newTexture;
                uploadedAvatarPath = path;
                selectedAvatarTexture = uploadedAvatarTexture;
                selectedAvatarIsUploaded = true;
                selectedPresetIndex = -1;
                applyAvatarToPlayer();
                showStatus("Uploaded: " + new File(path).getName(), 3f);
            } catch (Exception e) {
                showStatus("Image load failed", 3f);
            }
        });

        ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, event -> {
            if (gameState != GameState.LEADERBOARD_ENTRY) {
                return;
            }
            String reason = event.getPayloadOrNull(String.class);
            if (reason == null || reason.isBlank()) {
                reason = "image selection failed";
            }
            showStatus("Upload failed: " + reason, 3f);
        });
    }

    private void applyAvatarToPlayer() {
        Entity player = getPlayerEntity();
        if (player != null) {
            player.setTexture(selectedAvatarTexture);
        }
    }

    private Entity getPlayerEntity() {
        for (Entity entity : entityManager.getEntities()) {
            if (entity.getID() == PLAYER_ID) {
                return entity;
            }
        }
        return null;
    }

    private Color getEntityRenderColor(Entity entity) {
        if (entity == null) {
            return Color.WHITE;
        }
        if (entity.getID() == PLAYER_ID) {
            return new Color(0.75f, 0.9f, 1f, 1f);
        }
        if (entity.getCollidable() instanceof FoodCollidableComponent food) {
            switch (food.getFoodCategory()) {
                case VEGETABLE:
                    return new Color(0.2f, 0.85f, 0.2f, 1f);
                case PROTEIN:
                    return new Color(0.9f, 0.25f, 0.25f, 1f);
                case CARBOHYDRATE:
                    return new Color(0.95f, 0.8f, 0.2f, 1f);
                case OIL:
                    return new Color(0.72f, 0.42f, 0.9f, 1f);
                default:
                    return Color.WHITE;
            }
        }
        return Color.WHITE;
    }

    private void captureClick() {
        clickPending = Gdx.input.justTouched();
        if (clickPending) {
        	touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        	camera.unproject(touchPos);
        	clickX = touchPos.x;
            clickY = touchPos.y;
//            clickX = Gdx.input.getX();
//            clickY = Gdx.graphics.getHeight() - Gdx.input.getY();
        }
    }

    private boolean consumeClick(Rectangle bounds) {
        if (!clickPending) return false;
        if (!bounds.contains(clickX, clickY)) return false;
        clickPending = false;
        return true;
    }

    private void drawScreenPanel(Rectangle panel) {
        UiPanelRenderer.drawScreenPanel(shapeRenderer, panel);
    }

    private void drawActionButton(Rectangle bounds, Color fillColor) {
        UiPanelRenderer.drawActionButton(shapeRenderer, bounds, fillColor);
    }

    private void drawTextInputField(Rectangle bounds, boolean active) {
        UiPanelRenderer.drawTextInputField(shapeRenderer, bounds, active);
    }

    private void applyFullScreenProjection() {
        int logicalWidth = Gdx.graphics.getWidth();
        int logicalHeight = Gdx.graphics.getHeight();
        int pixelWidth = Gdx.graphics.getBackBufferWidth();
        int pixelHeight = Gdx.graphics.getBackBufferHeight();
        Gdx.gl.glViewport(0, 0, pixelWidth, pixelHeight);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, logicalWidth, logicalHeight);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
    }

    private void drawStatus(SpriteBatch targetBatch, float x, float y) {
        if (statusSecondsLeft <= 0f || statusMessage == null || statusMessage.isBlank()) return;
        font.draw(targetBatch, statusMessage, x, y);
    }

    private void showStatus(String message, float seconds) {
        statusMessage = message;
        statusSecondsLeft = seconds;
    }

    public void addFood(FoodCategory category, int scoreValue) {
        gameSession.addFood(category, scoreValue);
    }

    public boolean isPlateHealthy() {
        return gameSession.isPlateHealthy();
    }

    public void submitPlate() {
        boolean healthy = gameSession.isPlateHealthy();
        gameSession.submitPlate();
        if (healthy) {
            showStatus("Healthy plate! +" + gameSession.getHealthyScoreBonus() + " score, +"
                    + (int) gameSession.getHealthyTimerBonus() + "s. Plate reset.", 2.5f);
        } else {
            showStatus("Unhealthy plate. -" + (int) gameSession.getUnhealthyTimerPenalty()
                    + "s. Plate reset.", 2.5f);
        }
    }

    public void resetPlate() {
        gameSession.resetPlate();
    }

    private boolean isSceneKeyJustPressed(int mainKey, int numpadKey) {
        return Gdx.input.isKeyJustPressed(mainKey) || Gdx.input.isKeyJustPressed(numpadKey);
    }

    @Override
    public void resize(int width, int height) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);

        if (safeWidth != lastViewportWidth || safeHeight != lastViewportHeight) {
            rescaleEntitiesForViewportChange(lastViewportWidth, lastViewportHeight, safeWidth, safeHeight);
            lastViewportWidth = safeWidth;
            lastViewportHeight = safeHeight;
        }

        if (foodMenuScene != null) {
            foodMenuScene.resize(width, height);
        }
        if (avatarSetupScreen != null) {
            avatarSetupScreen.resize(width, height);
        }
    }

    private void rescaleEntitiesForViewportChange(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if (oldWidth <= 0 || oldHeight <= 0 || entityManager == null) {
            return;
        }

        double sx = (double) newWidth / (double) oldWidth;
        double sy = (double) newHeight / (double) oldHeight;

        double targetGameplayScale = Math.min(
                (double) newWidth / (double) Math.max(1, referenceViewportWidth),
                (double) newHeight / (double) Math.max(1, referenceViewportHeight));
        if (targetGameplayScale <= 0d) {
            targetGameplayScale = 1d;
        }
        double scaleDelta = targetGameplayScale / Math.max(0.0001d, currentGameplayScale);
        currentGameplayScale = targetGameplayScale;

        for (Entity entity : entityManager.getEntities()) {
            double newX = entity.getXPosition() * sx;
            double newY = entity.getYPosition() * sy;
            entity.setXPosition(newX);
            entity.setYPosition(newY);

            CollidableComponent collidable = entity.getCollidable();
            if (collidable != null) {
                collidable.setCollisionRadius(collidable.getCollisionRadius() * scaleDelta);
            }

            MovementComponent movement = entity.getMovement();
            if (movement != null) {
                movement.setSpeed(movement.getSpeed() * scaleDelta);
            }
        }
    }
    
    private DifficultyConfig toDifficultyConfig(DifficultyPreset preset) {
        return new DifficultyConfig(
                preset.startingTimer,
                preset.npcCount,
                preset.npcSpeed,
                preset.healthyScoreBonus,
                preset.healthyTimerBonus,
                preset.unhealthyTimerPenalty,
                preset.foodEntityCount);
    }
    
    @Override
    public void dispose() {
        for (LeaderboardEntry entry : leaderboardEntries) {
            if (entry.ownsTexture && entry.avatarTexture != null) {
                entry.avatarTexture.dispose();
            }
        }
        if (uploadedAvatarTexture != null) {
            uploadedAvatarTexture.dispose();
            uploadedAvatarTexture = null;
        }
        if (presetAvatars != null) {
            for (Texture texture : presetAvatars) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        if (foodCategoryTextures != null) {
            for (Texture texture : foodCategoryTextures.values()) {
                if (texture != null) {
                    texture.dispose();
                }
            }
            foodCategoryTextures.clear();
        }
        if (foodMenuScene != null) {
            foodMenuScene.dispose();
        }
        if (avatarSetupScreen != null) {
            avatarSetupScreen.dispose();
        }
        playerImageInputService = null;
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }
}
