package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.FoodCollidableComponent;
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
        EASY("Easy", 75f, 6, 95f, 8, 6f, 3f),
        NORMAL("Normal", 60f, 8, 120f, 10, 5f, 5f),
        HARD("Hard", 45f, 10, 150f, 12, 4f, 6f);

        final String label;
        final float startingTimer;
        final int npcCount;
        final float npcSpeed;
        final int healthyScoreBonus;
        final float healthyTimerBonus;
        final float unhealthyTimerPenalty;

        DifficultyPreset(String label,
                         float startingTimer,
                         int npcCount,
                         float npcSpeed,
                         int healthyScoreBonus,
                         float healthyTimerBonus,
                         float unhealthyTimerPenalty) {
            this.label = label;
            this.startingTimer = startingTimer;
            this.npcCount = npcCount;
            this.npcSpeed = npcSpeed;
            this.healthyScoreBonus = healthyScoreBonus;
            this.healthyTimerBonus = healthyTimerBonus;
            this.unhealthyTimerPenalty = unhealthyTimerPenalty;
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

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private StartMenuScene foodMenuScene;
    private AvatarSetupFlowScreen avatarSetupScreen;

    private InputOutputManager ioManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    private GameSession gameSession;
    private PlayerImageInputService playerImageInputService;

    private GameState gameState;
    private DifficultyPreset difficultyPreset;
    private boolean paused;

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

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager(entityManager, movementManager);
        difficultyPreset = DifficultyPreset.NORMAL;
        gameSession = new GameSession(
                difficultyPreset.startingTimer,
                difficultyPreset.healthyScoreBonus,
                difficultyPreset.healthyTimerBonus,
                difficultyPreset.unhealthyTimerPenalty);
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

        float centerX = Gdx.graphics.getWidth() / 2f;
        float topY = Gdx.graphics.getHeight() - 80f;

        Rectangle easyButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 130f, BUTTON_W, BUTTON_H);
        Rectangle normalButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 178f, BUTTON_W, BUTTON_H);
        Rectangle hardButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 226f, BUTTON_W, BUTTON_H);
        Rectangle backButton = new Rectangle(centerX - (BUTTON_W / 2f), 40f, BUTTON_W, BUTTON_H);

        if (consumeClick(easyButton)) {
            difficultyPreset = DifficultyPreset.EASY;
            showStatus("Difficulty set: Easy", 2f);
        }
        if (consumeClick(normalButton)) {
            difficultyPreset = DifficultyPreset.NORMAL;
            showStatus("Difficulty set: Normal", 2f);
        }
        if (consumeClick(hardButton)) {
            difficultyPreset = DifficultyPreset.HARD;
            showStatus("Difficulty set: Hard", 2f);
        }
        if (consumeClick(backButton)) {
            gameState = GameState.FOOD_MENU;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonRect(easyButton);
        drawButtonRect(normalButton);
        drawButtonRect(hardButton);
        drawButtonRect(backButton);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Difficulty Settings", centerX - 62f, topY);
        font.draw(batch, "Current: " + difficultyPreset.label, centerX - 52f, topY - 32f);
        font.draw(batch, "Easy  (75s, slower, +6s/-3s submit)", easyButton.x + 24f, easyButton.y + 24f);
        font.draw(batch, "Normal (60s, balanced, +5s/-5s submit)", normalButton.x + 26f, normalButton.y + 24f);
        font.draw(batch, "Hard  (45s, faster, +4s/-6s submit)", hardButton.x + 30f, hardButton.y + 24f);
        font.draw(batch, "Back to Main Menu", backButton.x + 62f, backButton.y + 24f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderHowToPlay() {
        applyFullScreenProjection();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float topY = Gdx.graphics.getHeight() - 80f;
        Rectangle backButton = new Rectangle(centerX - (BUTTON_W / 2f), 40f, BUTTON_W, BUTTON_H);

        if (consumeClick(backButton)) {
            gameState = GameState.FOOD_MENU;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonRect(backButton);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "How To Play", centerX - 38f, topY);
        font.draw(batch, "1. Click START from Food Menu.", 90f, topY - 45f);
        font.draw(batch, "2. Choose preset avatar or upload a custom image.", 90f, topY - 70f);
        font.draw(batch, "3. Click Start Game to begin simulation.", 90f, topY - 95f);
        font.draw(batch, "4. Move player with WASD keys.", 90f, topY - 120f);
        font.draw(batch, "5. Catch food items and build a healthy plate.", 90f, topY - 145f);
        font.draw(batch, "6. Press Enter to submit plate, R to reset plate.", 90f, topY - 170f);
        font.draw(batch, "7. Press Space to pause/resume.", 90f, topY - 195f);
        font.draw(batch, "8. When timer ends, submit your name/avatar to leaderboard.", 90f, topY - 220f);
        font.draw(batch, "Back to Main Menu", backButton.x + 62f, backButton.y + 24f);
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
            sceneManager.update(dt);
            collisionManager.update();
            entityManager.flushRemovals();

            float nextTimer = Math.max(0f, gameSession.getTimer() - dt);
            gameSession.setTimer(nextTimer);
            if (nextTimer <= 0f) {
                paused = false;
                gameState = GameState.LEADERBOARD_ENTRY;
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

        font.draw(batch, "Move with WASD", 20f, Gdx.graphics.getHeight() - 20f);
        font.draw(batch, "Space: Pause/Resume", 20f, Gdx.graphics.getHeight() - 38f);
        font.draw(batch, "Enter: Submit plate (resets plate) | R: Reset plate", 20f, Gdx.graphics.getHeight() - 56f);
        font.draw(batch, "Timer: " + (int) Math.ceil(gameSession.getTimer()), 20f, Gdx.graphics.getHeight() - 74f);
        font.draw(batch, "Score: " + gameSession.getScore(), 20f, Gdx.graphics.getHeight() - 92f);
        font.draw(batch, "Difficulty: " + difficultyPreset.label, 20f, Gdx.graphics.getHeight() - 110f);
        font.draw(batch, "Plate V/P/C/O: " + gameSession.getVegetableCount() + "/"
                + gameSession.getProteinCount() + "/" + gameSession.getCarbCount() + "/"
                + gameSession.getOilCount(), 20f, Gdx.graphics.getHeight() - 128f);
        font.draw(batch, "Target ranges: V 2-4 | P 1-3 | C 1-2 | O 0-1", 20f, Gdx.graphics.getHeight() - 146f);
        font.draw(batch, "Food legend: Green=Veg Red=Protein Yellow=Carb Purple=Oil", 20f, Gdx.graphics.getHeight() - 164f);

        if (paused) {
            font.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f - 24f, Gdx.graphics.getHeight() / 2f);
        }
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderLeaderboardEntry() {
        applyFullScreenProjection();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float topY = Gdx.graphics.getHeight() - 70f;

        Rectangle nameButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 130f, BUTTON_W, BUTTON_H);
        Rectangle uploadButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 180f, BUTTON_W, BUTTON_H);
        Rectangle submitButton = new Rectangle(centerX - (BUTTON_W / 2f), topY - 230f, BUTTON_W, BUTTON_H);

        if (consumeClick(nameButton)) {
            requestNameInput();
        }
        if (consumeClick(uploadButton)) {
            requestImageUpload("leaderboard-entry");
        }
        if (consumeClick(submitButton)) {
            submitLeaderboardEntry();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonRect(nameButton);
        drawButtonRect(uploadButton);
        drawButtonRect(submitButton);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Leaderboard Submission", centerX - 90f, topY);
        font.draw(batch, "Final Score: " + gameSession.getScore(), centerX - 60f, topY - 30f);
        font.draw(batch, "Name: " + (playerNameInput.isBlank() ? "<not set>" : playerNameInput), centerX - 120f, topY - 60f);

        if (selectedAvatarTexture != null) {
            font.draw(batch, "Avatar:", centerX - 120f, topY - 90f);
            batch.draw(selectedAvatarTexture, centerX - 50f, topY - 112f, 24f, 24f);
        } else {
            font.draw(batch, "Avatar: <none>", centerX - 120f, topY - 90f);
        }

        font.draw(batch, "Enter Name", nameButton.x + 90f, nameButton.y + 24f);
        font.draw(batch, "Upload / Change Image", uploadButton.x + 58f, uploadButton.y + 24f);
        font.draw(batch, "Submit to Leaderboard", submitButton.x + 55f, submitButton.y + 24f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderLeaderboardView() {
        applyFullScreenProjection();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float topY = Gdx.graphics.getHeight() - 60f;
        String footerLabel = leaderboardOpenedFromMenu ? "Back to Main Menu" : "Play Again";
        Rectangle footerButton = new Rectangle(centerX - (BUTTON_W / 2f), 30f, BUTTON_W, BUTTON_H);
        if (consumeClick(footerButton)) {
            gameState = GameState.FOOD_MENU;
            playerNameInput = "";
            leaderboardOpenedFromMenu = false;
            showStatus("Setup ready for next run", 2f);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonRect(footerButton);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Leaderboard", centerX - 45f, topY);

        int maxRows = Math.min(10, leaderboardEntries.size());
        float rowY = topY - 36f;
        for (int i = 0; i < maxRows; i++) {
            LeaderboardEntry entry = leaderboardEntries.get(i);
            font.draw(batch, (i + 1) + ".", centerX - 210f, rowY);
            if (entry.avatarTexture != null) {
                batch.draw(entry.avatarTexture, centerX - 190f, rowY - 16f, 20f, 20f);
            }
            font.draw(batch, entry.name, centerX - 160f, rowY);
            font.draw(batch, "Score: " + entry.score, centerX + 40f, rowY);
            rowY -= 30f;
        }

        if (leaderboardEntries.isEmpty()) {
            font.draw(batch, "No entries yet.", centerX - 45f, topY - 40f);
        }

        font.draw(batch, footerLabel, footerButton.x + 66f, footerButton.y + 24f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void startNewGame() {
        gameSession = new GameSession(
                difficultyPreset.startingTimer,
                difficultyPreset.healthyScoreBonus,
                difficultyPreset.healthyTimerBonus,
                difficultyPreset.unhealthyTimerPenalty);
        paused = false;
        playerNameInput = "";
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
        loadEntitiesForLevel(1);
        gameState = GameState.PLAYING;
        showStatus("Game started (" + difficultyPreset.label + ")", 2f);
    }

    private void loadEntitiesForLevel(int levelNum) {
        Entity player = new Entity(PLAYER_ID);
        player.setXPosition(200);
        player.setYPosition(200);
        player.setMovement(new PlayerMovement(ioManager, 250f));
        player.setCollidable(new PlayerCollidableComponent(15));
        if (selectedAvatarTexture != null) {
            player.setTexture(selectedAvatarTexture);
        }
        sceneManager.spawnEntity(player);

        java.util.Random rng = new java.util.Random();
        int npcCount = (levelNum == 1) ? difficultyPreset.npcCount : Math.max(4, difficultyPreset.npcCount / 2);
        for (int i = 0; i < npcCount; i++) {
            Entity npc = new Entity(100 + i);
            npc.setXPosition(100 + rng.nextInt(500));
            npc.setYPosition(100 + rng.nextInt(300));
            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;
            FoodCategory foodCategory = randomFoodCategory(rng);
            npc.setMovement(new AIMovement(difficultyPreset.npcSpeed, dirX, dirY));
            npc.setCollidable(new FoodCollidableComponent(8, foodCategory, 1, gameSession));
            Texture foodTexture = (foodCategoryTextures != null) ? foodCategoryTextures.get(foodCategory) : null;
            if (foodTexture != null) {
                npc.setTexture(foodTexture);
            }
            sceneManager.spawnEntity(npc);
        }
    }

    private FoodCategory randomFoodCategory(java.util.Random rng) {
        FoodCategory[] categories = FoodCategory.values();
        return categories[rng.nextInt(categories.length)];
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

    private void requestNameInput() {
        Gdx.input.getTextInput(new TextInputListener() {
            @Override
            public void input(String text) {
                if (text == null) return;
                playerNameInput = text.trim();
                if (playerNameInput.length() > 24) {
                    playerNameInput = playerNameInput.substring(0, 24);
                }
                showStatus(playerNameInput.isBlank() ? "Name cleared" : "Name set: " + playerNameInput, 2f);
            }

            @Override
            public void canceled() {
                showStatus("Name input cancelled", 2f);
            }
        }, "Enter Name", playerNameInput, "Player name");
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

        FileHandle file = Gdx.files.local(LEADERBOARD_FILE);
        if (!file.exists()) {
            return;
        }

        String content = file.readString("UTF-8");
        String[] lines = content.split("\\r?\\n");
        for (String rawLine : lines) {
            if (rawLine == null || rawLine.isBlank()) {
                continue;
            }

            String[] parts = rawLine.split("\\t", -1);
            if (parts.length < 4) {
                continue;
            }

            String name = parts[0].trim();
            int score;
            int presetIndex;
            try {
                score = Integer.parseInt(parts[1].trim());
                presetIndex = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            String uploadedPath = parts[3].trim();
            if (uploadedPath.isBlank()) {
                uploadedPath = null;
            }

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
        StringBuilder sb = new StringBuilder();
        for (LeaderboardEntry entry : leaderboardEntries) {
            String safeName = sanitizeName(entry.name);
            int safePreset = entry.presetIndex;
            String safePath = entry.uploadedPath == null ? "" : entry.uploadedPath.replace('\t', ' ').replace('\n', ' ');
            sb.append(safeName)
                    .append('\t')
                    .append(entry.score)
                    .append('\t')
                    .append(safePreset)
                    .append('\t')
                    .append(safePath)
                    .append('\n');
        }

        Gdx.files.local(LEADERBOARD_FILE).writeString(sb.toString(), false, "UTF-8");
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
            clickX = Gdx.input.getX();
            clickY = Gdx.graphics.getHeight() - Gdx.input.getY();
        }
    }

    private boolean consumeClick(Rectangle bounds) {
        if (!clickPending) return false;
        if (!bounds.contains(clickX, clickY)) return false;
        clickPending = false;
        return true;
    }

    private void drawButtonRect(Rectangle bounds) {
        shapeRenderer.setColor(0f, 0f, 0f, 0.45f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(Color.WHITE);
    }

    private void applyFullScreenProjection() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(0, 0, width, height);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
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
        if (foodMenuScene != null) {
            foodMenuScene.resize(width, height);
        }
        if (avatarSetupScreen != null) {
            avatarSetupScreen.resize(width, height);
        }
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
