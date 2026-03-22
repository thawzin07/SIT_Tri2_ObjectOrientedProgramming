package com.sit.inf1009.project.app;

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
import com.sit.inf1009.project.app.controllers.GameFlowController;
import com.sit.inf1009.project.app.controllers.GameplayController;
import com.sit.inf1009.project.app.controllers.LeaderboardController;
import com.sit.inf1009.project.app.controllers.SettingsController;
import com.sit.inf1009.project.app.runtime.GameplayRuntime;
import com.sit.inf1009.project.app.ui.AppUiRenderer;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;
import com.sit.inf1009.project.engine.core.handlers.LibGdxMouseInputHandler;
import com.sit.inf1009.project.engine.core.handlers.PlayerImageInputService;
import com.sit.inf1009.project.engine.core.handlers.SoundOutputHandler;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.FoodCategory;
import com.sit.inf1009.project.game.domain.GameSession;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.game.persistence.LeaderboardFileStore;
import com.sit.inf1009.project.game.persistence.LeaderboardRecord;
import com.sit.inf1009.project.game.services.FoodSpawnCoordinator;
import com.sit.inf1009.project.game.ui.LeaderboardNameEditor;
import com.sit.inf1009.project.game.ui.screens.AvatarSetupFlowScreen;
import com.sit.inf1009.project.game.ui.screens.StartMenuScene;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main extends ApplicationAdapter {

    private static class LeaderboardEntry implements AppUiRenderer.LeaderboardRow {
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

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public Texture getAvatarTexture() {
            return avatarTexture;
        }
    }

    private static final int PLAYER_ID = 1;
    private static final float BUTTON_W = 250f;
    private static final float BUTTON_H = 38f;
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private final GameFlowController flowController = new GameFlowController(GameState.FOOD_MENU);
    private final SettingsController settingsController = new SettingsController(DifficultyPreset.NORMAL);
    private final GameplayController gameplayController = new GameplayController();
    private final LeaderboardController leaderboardController = new LeaderboardController(new LeaderboardFileStore());

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private StartMenuScene foodMenuScene;
    private AvatarSetupFlowScreen avatarSetupScreen;
    private OrthographicCamera camera;
    private Vector3 touchPos = new Vector3();
    private AppUiRenderer appUiRenderer;

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
    private boolean leaderboardOpenedFromMenu;
    private String activeBackgroundTrack;

    private boolean leaderboardNameEditing;
    private int lastViewportWidth;
    private int lastViewportHeight;
    
    private static final int foodId = 100;
    private GameplayRuntime gameplayRuntime;
    private FoodSpawnCoordinator foodSpawnCoordinator;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        appUiRenderer = new AppUiRenderer(shapeRenderer, batch, font, camera, flowController);

        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager();
        foodSpawnCoordinator = new FoodSpawnCoordinator(entityManager, movementManager);
        difficultyPreset = settingsController.getPreset();
        difficultyConfig = settingsController.getConfig();
        gameState = flowController.getState();
        gameSession = gameplayController.createSession(settingsController.getConfig());
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
        gameplayRuntime = new GameplayRuntime(
                ioManager,
                entityManager,
                movementManager,
                sceneManager,
                foodSpawnCoordinator,
                foodCategoryTextures,
                PLAYER_ID,
                foodId);
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
        gameplayRuntime.initViewportBaseline(lastViewportWidth, lastViewportHeight);
        gameState = GameState.FOOD_MENU;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);

        float dt = Gdx.graphics.getDeltaTime();
        flowController.tickStatus(dt);
        syncBackgroundMusicForState();

        appUiRenderer.captureClick(touchPos);

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
        AppUiRenderer.DifficultyAction action = appUiRenderer.renderDifficultySettings(difficultyPreset);
        if (action != AppUiRenderer.DifficultyAction.NONE) {
            playButtonClick();
        }
        switch (action) {
            case SET_EASY:
                difficultyPreset = DifficultyPreset.EASY;
                settingsController.setPreset(difficultyPreset);
                difficultyConfig = settingsController.getConfig();
                showStatus("Difficulty set: Easy", 2f);
                break;
            case SET_NORMAL:
                difficultyPreset = DifficultyPreset.NORMAL;
                settingsController.setPreset(difficultyPreset);
                difficultyConfig = settingsController.getConfig();
                showStatus("Difficulty set: Normal", 2f);
                break;
            case SET_HARD:
                difficultyPreset = DifficultyPreset.HARD;
                settingsController.setPreset(difficultyPreset);
                difficultyConfig = settingsController.getConfig();
                showStatus("Difficulty set: Hard", 2f);
                break;
            case BACK_TO_MENU:
                gameState = GameState.FOOD_MENU;
                break;
            default:
                break;
        }
    }

    private void renderHowToPlay() {
        AppUiRenderer.HowToPlayAction action = appUiRenderer.renderHowToPlay(rulesOpenedFromPause);
        if (action != AppUiRenderer.HowToPlayAction.NONE) {
            playButtonClick();
        }
        switch (action) {
            case BACK_TO_PAUSE:
                gameState = GameState.PLAYING;
                rulesOpenedFromPause = false;
                break;
            case BACK_TO_MENU:
                gameState = GameState.FOOD_MENU;
                break;
            default:
                break;
        }
    }

    private void renderGameplay(float dt) {
        appUiRenderer.applyFullScreenProjection();

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
            gameplayRuntime.loadEntitiesForLevel(3, gameSession, difficultyConfig, selectedAvatarTexture);
        }
        if (isSceneKeyJustPressed(Input.Keys.NUM_2, Input.Keys.NUMPAD_2)) {
            sceneManager.push(new Scene("Level 2", Color.MAROON));
            gameplayRuntime.loadEntitiesForLevel(2, gameSession, difficultyConfig, selectedAvatarTexture);
        }
        if (isSceneKeyJustPressed(Input.Keys.NUM_1, Input.Keys.NUMPAD_1)) {
            sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
            gameplayRuntime.loadEntitiesForLevel(1, gameSession, difficultyConfig, selectedAvatarTexture);
        }

        if (!paused) {
            movementManager.updateAll(dt);
            sceneManager.update(dt, entityManager.getEntities());
            collisionManager.update();
            entityManager.flushRemovals();
            gameplayRuntime.ensureFoodDiversityAndCount(difficultyConfig);

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
            shapeRenderer.setColor(gameplayRuntime.getEntityRenderColor(e));
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
        font.draw(batch, "Difficulty: " + difficultyPreset.getLabel(), 20f, Gdx.graphics.getHeight() - 56f);
        font.draw(batch, "Plate V/P/C/O: " + gameSession.getVegetableCount() + "/"
                + gameSession.getProteinCount() + "/" + gameSession.getCarbCount() + "/"
                + gameSession.getOilCount(), 20f, Gdx.graphics.getHeight() - 74f);

        appUiRenderer.drawStatus(20f, 24f);
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

            if (appUiRenderer.consumeClick(resumeBtn)) {
                playButtonClick();
                paused = false;
            }
            if (appUiRenderer.consumeClick(restartBtn)) {
                playButtonClick();
                startNewGame();
            }
            if (appUiRenderer.consumeClick(rulesBtn)) {
                playButtonClick();
                rulesOpenedFromPause = true;
                gameState = GameState.HOW_TO_PLAY;
            }
            if (appUiRenderer.consumeClick(quitBtn)) {
                playButtonClick();
                gameState = GameState.FOOD_MENU;
                paused = false;
            }

            appUiRenderer.drawScreenPanel(panel);
            appUiRenderer.drawActionButton(resumeBtn, new Color(0.16f, 0.62f, 0.2f, 1f));  // Green
            appUiRenderer.drawActionButton(restartBtn, new Color(0.1f, 0.45f, 0.78f, 1f)); // Blue
            appUiRenderer.drawActionButton(rulesBtn, new Color(0.6f, 0.4f, 0.1f, 1f));     // Orange
            appUiRenderer.drawActionButton(quitBtn, new Color(0.75f, 0.22f, 0.22f, 1f));   // Red

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
        handleLeaderboardNameTyping();
        AppUiRenderer.LeaderboardEntryAction action = appUiRenderer.renderLeaderboardEntry(
                gameSession.getScore(),
                selectedAvatarTexture,
                playerNameInput,
                leaderboardNameEditing);
        if (action != AppUiRenderer.LeaderboardEntryAction.NONE) {
            playButtonClick();
        }
        switch (action) {
            case ENABLE_NAME_EDIT:
                leaderboardNameEditing = true;
                showStatus("Typing enabled. Press Enter to confirm name.", 2.5f);
                break;
            case REQUEST_UPLOAD:
                leaderboardNameEditing = false;
                requestImageUpload("leaderboard-entry");
                break;
            case SUBMIT:
                leaderboardNameEditing = false;
                submitLeaderboardEntry();
                break;
            case BACK_TO_MENU:
                leaderboardNameEditing = false;
                gameState = GameState.FOOD_MENU;
                break;
            default:
                break;
        }
    }

    private void renderLeaderboardView() {
        AppUiRenderer.LeaderboardViewAction action =
                appUiRenderer.renderLeaderboardView(leaderboardEntries, leaderboardOpenedFromMenu);
        if (action == AppUiRenderer.LeaderboardViewAction.FOOTER_CLICKED) {
            playButtonClick();
            gameState = GameState.FOOD_MENU;
            playerNameInput = "";
            leaderboardNameEditing = false;
            leaderboardOpenedFromMenu = false;
            showStatus("Setup ready for next run", 2f);
        }
    }

    private void startNewGame() {
        gameSession = gameplayController.createSession(difficultyConfig);
        paused = false;
        leaderboardNameEditing = false;
        playerNameInput = "";
        gameplayRuntime.startNewGame(gameSession, difficultyConfig, selectedAvatarTexture);
        gameState = GameState.PLAYING;
        showStatus("Game started (" + difficultyPreset.getLabel() + ")", 2f);
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
                leaderboardController.sanitizeName(playerNameInput),
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
        List<LeaderboardRecord> records = leaderboardController.load(LEADERBOARD_FILE);
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
                    leaderboardController.sanitizeName(entry.name),
                    entry.score,
                    entry.presetIndex,
                    entry.uploadedPath));
        }
        leaderboardController.save(LEADERBOARD_FILE, records);
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

    private void showStatus(String message, float seconds) {
        flowController.showStatus(message, seconds);
    }

    private void playButtonClick() {
        ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));
    }

    private void syncBackgroundMusicForState() {
        String nextTrack = backgroundTrackForState(gameState);
        if (Objects.equals(nextTrack, activeBackgroundTrack)) {
            return;
        }

        if (activeBackgroundTrack != null) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_STOP, activeBackgroundTrack));
        }
        if (nextTrack != null) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, nextTrack));
        }
        activeBackgroundTrack = nextTrack;
    }

    private String backgroundTrackForState(GameState state) {
        if (state == null) {
            return null;
        }

        return switch (state) {
            case FOOD_MENU -> "foodmenumusic";
            case DIFFICULTY_SETTINGS -> "settingmusic";
            case HOW_TO_PLAY -> "howtoplaymusic";
            case LEADERBOARD_ENTRY, LEADERBOARD_VIEW -> "leaderboardmusic";
            default -> null;
        };
    }

    public void addFood(FoodCategory category, int scoreValue) {
        gameplayController.addFood(gameSession, category, scoreValue);
    }

    public boolean isPlateHealthy() {
        return gameplayController.isPlateHealthy(gameSession);
    }

    public void submitPlate() {
        GameplayController.PlateSubmitResult result = gameplayController.submitPlate(gameSession);
        if (result.isHealthy()) {
            showStatus("Healthy plate! +" + result.getHealthyScoreBonus() + " score, +"
                    + (int) result.getTimerDeltaSeconds() + "s. Plate reset.", 2.5f);
        } else {
            showStatus("Unhealthy plate. -" + (int) Math.abs(result.getTimerDeltaSeconds())
                    + "s. Plate reset.", 2.5f);
        }
    }

    public void resetPlate() {
        gameplayController.resetPlate(gameSession);
    }

    private boolean isSceneKeyJustPressed(int mainKey, int numpadKey) {
        return Gdx.input.isKeyJustPressed(mainKey) || Gdx.input.isKeyJustPressed(numpadKey);
    }

    @Override
    public void resize(int width, int height) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);

        if (safeWidth != lastViewportWidth || safeHeight != lastViewportHeight) {
            gameplayRuntime.rescaleEntitiesForViewportChange(lastViewportWidth, lastViewportHeight, safeWidth, safeHeight);
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

    @Override
    public void dispose() {
        ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_STOP_ALL, null));
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
