package com.sit.inf1009.project.app;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.sit.inf1009.project.app.controllers.GameFlowController;
import com.sit.inf1009.project.app.flow.AvatarFlowOrchestrator;
import com.sit.inf1009.project.app.flow.GameplayLoopOrchestrator;
import com.sit.inf1009.project.app.flow.LeaderboardFlowOrchestrator;
import com.sit.inf1009.project.app.flow.StateFlowOrchestrator;
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
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.game.persistence.LeaderboardFileStore;
import com.sit.inf1009.project.game.persistence.LeaderboardStore;
import com.sit.inf1009.project.game.services.FoodSpawnCoordinator;
import com.sit.inf1009.project.game.services.TutorialCoordinator;
import com.sit.inf1009.project.game.ui.TutorialUiRenderer;
import com.sit.inf1009.project.game.ui.screens.AvatarSetupFlowScreen;
import com.sit.inf1009.project.game.ui.screens.StartMenuScene;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Main extends ApplicationAdapter {

    private static final int PLAYER_ID = 1;
    private static final float BUTTON_W = 250f;
    private static final float BUTTON_H = 38f;
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private final GameFlowController flowController = new GameFlowController(GameState.FOOD_MENU);
    private final LeaderboardStore leaderboardStore = new LeaderboardFileStore();

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
    private float musicVolume = 1.0f;
    private boolean rulesOpenedFromStart = false;

    private Texture[] presetAvatars;
    private String[] presetAvatarLabels;
    private Map<FoodCategory, Texture> foodCategoryTextures;
    private Texture uploadedAvatarTexture;
    private String uploadedAvatarPath;
    private Texture selectedAvatarTexture;
    private boolean selectedAvatarIsUploaded;
    private int selectedPresetIndex;

    private final List<LeaderboardFlowOrchestrator.LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    private String playerNameInput = "";
    private boolean leaderboardOpenedFromMenu;
    private String activeBackgroundTrack;

    private boolean leaderboardNameEditing;
    private int lastViewportWidth;
    private int lastViewportHeight;
    
    private static final int foodId = 100;
    private GameplayRuntime gameplayRuntime;
    private FoodSpawnCoordinator foodSpawnCoordinator;

    private TutorialCoordinator tutorialCoordinator;
    private TutorialUiRenderer tutorialUiRenderer;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        appUiRenderer = new AppUiRenderer(shapeRenderer, batch, font, camera, flowController);

        ioManager = new InputOutputManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager(entityManager, ioManager);
        sceneManager = new SceneManager();
        foodSpawnCoordinator = new FoodSpawnCoordinator(entityManager, movementManager);
        difficultyPreset = DifficultyPreset.NORMAL;
        difficultyConfig = difficultyPreset.toConfig();
        gameState = flowController.getState();
        gameSession = GameSession.fromConfig(difficultyConfig);
        paused = false;

        tutorialCoordinator = new TutorialCoordinator();
        tutorialUiRenderer = new TutorialUiRenderer();

        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
        playerImageInputService = new PlayerImageInputService(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler());
        ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_SET_MUSIC_VOLUME, musicVolume));

        presetAvatarLabels = new String[] { "", "", "" };
        presetAvatars = new Texture[] {
                loadTextureWithFallback("avators/defaultavator1.png", "droplet.png"),
                loadTextureWithFallback("avators/defaultavator2.png", "bucket.png"),
                loadTextureWithFallback("avators/defaultavator3.png", "libgdx.png")
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
                rulesOpenedFromStart = true;
                gameState = GameState.HOW_TO_PLAY;
            }

            @Override
            public void onDifficulty() {
                gameState = GameState.DIFFICULTY_SETTINGS;
            }

            @Override
            public void onHowToPlay() {
                rulesOpenedFromStart = false;
                gameState = GameState.TUTORIAL;
            }

            @Override
            public void onHighScores() {
                leaderboardOpenedFromMenu = true;
                gameState = GameState.LEADERBOARD_VIEW;
            }

            @Override
            public void onCredits() {
                gameState = GameState.CREDITS;
            }
            
            @Override
            public void onQuit() {
                Gdx.app.exit();
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
            case CREDITS:
                renderCredits();
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
            case TUTORIAL:
                startTutorial();
                break;
            default:
                break;
        }
    }

    private void renderDifficultySettings() {
        AppUiRenderer.DifficultyRenderResult result = appUiRenderer.renderDifficultySettings(difficultyPreset, musicVolume);
        if (result.musicVolumeChanged()) {
            musicVolume = result.musicVolume();
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_SET_MUSIC_VOLUME, musicVolume));
        }
        AppUiRenderer.DifficultyAction action = result.action();
        if (action != AppUiRenderer.DifficultyAction.NONE) {
            playButtonClick();
        }
        switch (action) {
            case SET_EASY:
                difficultyPreset = DifficultyPreset.EASY;
                difficultyConfig = difficultyPreset.toConfig();
                showStatus("Difficulty set: Easy", 2f);
                break;
            case SET_NORMAL:
                difficultyPreset = DifficultyPreset.NORMAL;
                difficultyConfig = difficultyPreset.toConfig();
                showStatus("Difficulty set: Normal", 2f);
                break;
            case SET_HARD:
                difficultyPreset = DifficultyPreset.HARD;
                difficultyConfig = difficultyPreset.toConfig();
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
        AppUiRenderer.HowToPlayAction action = appUiRenderer.renderHowToPlay(rulesOpenedFromPause, rulesOpenedFromStart);
        
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
                rulesOpenedFromStart = false;
                break;
            case CONTINUE_TO_AVATAR:
                gameState = GameState.AVATAR_SETUP;
                rulesOpenedFromStart = false;
                break;
            default:
                break;
        }
    }

    private void renderGameplay(float dt) {
        appUiRenderer.applyFullScreenProjection();

        if (!tutorialCoordinator.isFinished()) {
            paused = GameplayLoopOrchestrator.togglePauseOnEsc(paused);
        }

        if (!paused && !tutorialCoordinator.isFinished()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                submitPlate();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resetPlate();
                showStatus("Plate reset", 2f);
            }
        }

        DifficultyConfig activeConfig = tutorialCoordinator.isActive()
                ? tutorialCoordinator.getConfig()
                : difficultyConfig;

        DifficultyPreset activePreset = tutorialCoordinator.isActive()
                ? tutorialCoordinator.getPreset()
                : difficultyPreset;

        boolean timerExpired = GameplayLoopOrchestrator.tickSimulation(
                dt,
                paused,
                movementManager,
                sceneManager,
                entityManager,
                collisionManager,
                gameplayRuntime,
                activeConfig,
                gameSession);
        if (timerExpired && !tutorialCoordinator.isActive()) {
            paused = false;
            gameState = GameState.LEADERBOARD_ENTRY;
            leaderboardNameEditing = true;
            showStatus("Time up! Enter name and submit to leaderboard", 4f);
        }

        Runnable tutorialBackgroundRenderer = null;
        if (tutorialCoordinator.isActive() && !tutorialCoordinator.isFinished()) {
            tutorialBackgroundRenderer = () -> tutorialUiRenderer.renderTutorialInstructionsPanel(
                    shapeRenderer,
                    batch,
                    font,
                    foodCategoryTextures,
                    tutorialCoordinator.getState()
            );
        }

        GameplayLoopOrchestrator.HudAction hudAction = GameplayLoopOrchestrator.renderWorldAndHud(
                shapeRenderer,
                batch,
                font,
                entityManager,
                sceneManager,
                gameplayRuntime,
                gameSession,
                activePreset,
                appUiRenderer,
                tutorialBackgroundRenderer);

        if (hudAction == GameplayLoopOrchestrator.HudAction.PAUSE_CLICKED && !tutorialCoordinator.isFinished()) {
            paused = !paused;
        }

        if (tutorialCoordinator.isActive() && tutorialCoordinator.isFinished()) {
            paused = true;
            Rectangle continueButton = tutorialUiRenderer.createContinueButton();

            if (appUiRenderer.consumeClick(continueButton)) {
                playButtonClick();
                tutorialCoordinator.stop();
                paused = false;
                gameState = GameState.AVATAR_SETUP;
                return;
            }

            tutorialUiRenderer.renderTutorialCompleteOverlay(
                    shapeRenderer,
                    batch,
                    font,
                    continueButton
            );
            return;
        }

        if (paused) {
            GameplayLoopOrchestrator.PauseAction action =
                    GameplayLoopOrchestrator.renderPauseMenu(appUiRenderer, batch, font);
            switch (action) {
                case RESUME -> {
                    playButtonClick();
                    paused = false;
                }
                case RESTART -> {
                    playButtonClick();
                    if (tutorialCoordinator.isActive()) {
                        startTutorial();
                    } else {
                        startNewGame();
                    }
                }
                case OPEN_RULES -> {
                    playButtonClick();
                    rulesOpenedFromPause = true;
                    gameState = GameState.HOW_TO_PLAY;
                }
                case QUIT_TO_MENU -> {
                    playButtonClick();
                    tutorialCoordinator.stop();
                    gameState = GameState.FOOD_MENU;
                    paused = false;
                }
                default -> {
                }
            }
        }
    }

    private void renderLeaderboardEntry() {
        LeaderboardFlowOrchestrator.NameEditState updated =
                LeaderboardFlowOrchestrator.updateNameTyping(gameState, leaderboardNameEditing, playerNameInput);
        playerNameInput = updated.playerNameInput();
        leaderboardNameEditing = updated.leaderboardNameEditing();
        if (updated.confirmed()) {
            showStatus("Name confirmed", 2f);
        } else if (updated.canceled()) {
            showStatus("Name edit cancelled", 2f);
        }
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

    private void renderCredits() {
        AppUiRenderer.CreditsAction action = appUiRenderer.renderCredits();
        if (action == AppUiRenderer.CreditsAction.BACK_TO_MENU) {
            playButtonClick();
            gameState = GameState.FOOD_MENU;
        }
    }

    private void startNewGame() {
        tutorialCoordinator.stop();
        gameSession = GameSession.fromConfig(difficultyConfig);
        paused = false;
        leaderboardNameEditing = false;
        playerNameInput = "";
        gameplayRuntime.startNewGame(gameSession, difficultyConfig, selectedAvatarTexture);
        gameState = GameState.PLAYING;
        showStatus("Game started (" + difficultyPreset.getLabel() + ")", 2f);
    }

    private void startTutorial() {
        DifficultyConfig tutorialConfig = tutorialCoordinator.getConfig();
        tutorialCoordinator.start();
        gameSession = GameSession.fromConfig(tutorialConfig);
        paused = false;
        leaderboardNameEditing = false;
        playerNameInput = "";
        gameplayRuntime.startNewGame(gameSession, tutorialConfig, selectedAvatarTexture);
        gameState = GameState.PLAYING;
        showStatus("Tutorial started", 2f);
    }

    private Map<FoodCategory, Texture> createFoodCategoryTextures() {
        Map<FoodCategory, Texture> textures = new EnumMap<>(FoodCategory.class);
        textures.put(FoodCategory.VEGETABLE, new Texture(Gdx.files.internal("vege.png")));
        textures.put(FoodCategory.PROTEIN, new Texture(Gdx.files.internal("protein.png")));
        textures.put(FoodCategory.CARBOHYDRATE, new Texture(Gdx.files.internal("carb.png")));
        textures.put(FoodCategory.OIL, new Texture(Gdx.files.internal("oil.png")));
        return textures;
    }

    private Texture loadTextureWithFallback(String preferredPath, String fallbackPath) {
        FileHandle preferred = Gdx.files.internal(preferredPath);
        if (preferred.exists()) {
            return new Texture(preferred);
        }
        return new Texture(Gdx.files.internal(fallbackPath));
    }

    private void applyAvatarSelection(AvatarSetupFlowScreen.SelectionResult result) {
        AvatarFlowOrchestrator.AvatarSelectionState state = AvatarFlowOrchestrator.applyAvatarSelection(
                result,
                new AvatarFlowOrchestrator.AvatarSelectionState(
                        uploadedAvatarTexture,
                        uploadedAvatarPath,
                        selectedAvatarTexture,
                        selectedAvatarIsUploaded,
                        selectedPresetIndex),
                presetAvatars,
                PLAYER_ID,
                entityManager);
        uploadedAvatarTexture = state.uploadedAvatarTexture();
        uploadedAvatarPath = state.uploadedAvatarPath();
        selectedAvatarTexture = state.selectedAvatarTexture();
        selectedAvatarIsUploaded = state.selectedAvatarIsUploaded();
        selectedPresetIndex = state.selectedPresetIndex();

        if (result != null && !result.isUploaded() && selectedPresetIndex >= 0 && selectedPresetIndex < presetAvatars.length) {
            showStatus("Preset avatar selected", 2f);
        }
    }

    private void selectPresetAvatar(int index) {
        AvatarFlowOrchestrator.AvatarSelectionState state = AvatarFlowOrchestrator.selectPresetAvatar(
                index,
                new AvatarFlowOrchestrator.AvatarSelectionState(
                        uploadedAvatarTexture,
                        uploadedAvatarPath,
                        selectedAvatarTexture,
                        selectedAvatarIsUploaded,
                        selectedPresetIndex),
                presetAvatars,
                PLAYER_ID,
                entityManager);
        uploadedAvatarTexture = state.uploadedAvatarTexture();
        uploadedAvatarPath = state.uploadedAvatarPath();
        selectedAvatarTexture = state.selectedAvatarTexture();
        selectedAvatarIsUploaded = state.selectedAvatarIsUploaded();
        selectedPresetIndex = state.selectedPresetIndex();
        if (selectedPresetIndex >= 0 && selectedPresetIndex < presetAvatars.length) {
            showStatus("Preset avatar selected", 2f);
        }
    }

    private void requestImageUpload(String sourceTag) {
        ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, sourceTag));
    }

    private void submitLeaderboardEntry() {
        LeaderboardFlowOrchestrator.SubmitResult result = LeaderboardFlowOrchestrator.submitEntry(
                leaderboardEntries,
                playerNameInput,
                gameSession.getScore(),
                selectedAvatarTexture,
                selectedAvatarIsUploaded,
                selectedPresetIndex,
                uploadedAvatarPath);
        if (!result.submitted()) {
            showStatus(result.failureReason(), 3f);
            return;
        }
        leaderboardEntries.clear();
        leaderboardEntries.addAll(result.entries());
        saveLeaderboardEntries();
        leaderboardOpenedFromMenu = false;
        gameState = GameState.LEADERBOARD_VIEW;
        showStatus("Leaderboard entry submitted", 2f);
    }

    private void loadLeaderboardEntries() {
        leaderboardEntries.clear();
        leaderboardEntries.addAll(LeaderboardFlowOrchestrator.loadEntries(leaderboardStore, LEADERBOARD_FILE, presetAvatars));
    }

    private void saveLeaderboardEntries() {
        LeaderboardFlowOrchestrator.saveEntries(leaderboardStore, LEADERBOARD_FILE, leaderboardEntries);
    }

    private void wirePlayerImageSelectionEvents() {
        ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTED, event -> {
            if (gameState != GameState.LEADERBOARD_ENTRY) {
                return;
            }
            String path = event.requirePayload(String.class);
            AvatarFlowOrchestrator.AvatarSelectionState state = AvatarFlowOrchestrator.applyUploadedImagePath(
                    path,
                    new AvatarFlowOrchestrator.AvatarSelectionState(
                            uploadedAvatarTexture,
                            uploadedAvatarPath,
                            selectedAvatarTexture,
                            selectedAvatarIsUploaded,
                            selectedPresetIndex),
                    PLAYER_ID,
                    entityManager);
            if (state.selectedAvatarTexture() != selectedAvatarTexture || state.selectedAvatarIsUploaded()) {
                uploadedAvatarTexture = state.uploadedAvatarTexture();
                uploadedAvatarPath = state.uploadedAvatarPath();
                selectedAvatarTexture = state.selectedAvatarTexture();
                selectedAvatarIsUploaded = state.selectedAvatarIsUploaded();
                selectedPresetIndex = state.selectedPresetIndex();
                showStatus("Uploaded: " + new File(path).getName(), 3f);
            } else {
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
        for (Entity entity : entityManager.getEntities()) {
            if (entity.getID() == PLAYER_ID) {
                entity.setTexture(selectedAvatarTexture);
                return;
            }
        }
    }

    private void showStatus(String message, float seconds) {
        flowController.showStatus(message, seconds);
    }

    private void playButtonClick() {
        StateFlowOrchestrator.playButtonClick(ioManager);
    }

    private void syncBackgroundMusicForState() {
        activeBackgroundTrack = StateFlowOrchestrator.syncBackgroundMusicForState(ioManager, gameState, activeBackgroundTrack);
    }

    public void addFood(FoodCategory category, int scoreValue) {
        if (gameSession != null && category != null) {
            gameSession.addFood(category, scoreValue);
        }
    }

    public boolean isPlateHealthy() {
        return gameSession != null && gameSession.isPlateHealthy();
    }

    public void submitPlate() {
        if (tutorialCoordinator.isActive()) {
            TutorialCoordinator.TutorialSubmitResult tutorialResult = tutorialCoordinator.submit(gameSession);

            if (tutorialResult.message() != null) {
                showStatus(tutorialResult.message(), tutorialResult.finished() ? 2.5f : 3f);
            }

            if (tutorialResult.finished()) {
                paused = true;
            }

            return;
        }

        GameSession.PlateSubmitResult result = gameSession == null
                ? new GameSession.PlateSubmitResult(false, 0, 0f)
                : gameSession.submitPlate();
        if (result.isHealthy()) {
            showStatus("Healthy plate! +" + result.getHealthyScoreBonus() + " score, +"
                    + (int) result.getTimerDeltaSeconds() + "s. Plate reset.", 2.5f);
        } else {
            showStatus("Unhealthy plate. -" + (int) Math.abs(result.getTimerDeltaSeconds())
                    + "s. Plate reset.", 2.5f);
        }
    }

    public void resetPlate() {
        if (gameSession != null) {
            gameSession.resetPlate();
        }
    }

    @Override
    public void resize(int width, int height) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);

        if (camera != null) {
            camera.setToOrtho(false, safeWidth, safeHeight);
            camera.update();
        }

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
        for (LeaderboardFlowOrchestrator.LeaderboardEntry entry : leaderboardEntries) {
            if (entry.ownsTexture() && entry.getAvatarTexture() != null) {
                entry.getAvatarTexture().dispose();
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
        if (appUiRenderer != null) {
            appUiRenderer.dispose();
        }
        playerImageInputService = null;
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }
}
