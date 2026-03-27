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
import com.sit.inf1009.project.engine.core.handlers.KeyboardInputHandler;
import com.sit.inf1009.project.engine.core.handlers.LibGdxMouseInputHandler;
import com.sit.inf1009.project.engine.core.handlers.SoundOutputHandler;
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


    private DifficultyPreset difficultyPreset;
    private DifficultyConfig difficultyConfig;
    private float musicVolume = 1.0f;

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
    private String activeBackgroundTrack;

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
        gameSession = GameSession.fromConfig(difficultyConfig);
        flowController.resume();

        tutorialCoordinator = new TutorialCoordinator();
        tutorialUiRenderer = new TutorialUiRenderer();

        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
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
            	flowController.openHowToPlayFromStart();
            }

            @Override
            public void onDifficulty() {
            	flowController.openDifficultySettings();
            }

            @Override
            public void onHowToPlay() {
                flowController.openTutorial();
            }

            @Override
            public void onHighScores() {
                flowController.openLeaderboard(true);
            }

            @Override
            public void onCredits() {
            	flowController.openCredits();
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
            	flowController.goToMainMenu();
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
        flowController.goToMainMenu();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);

        float dt = Gdx.graphics.getDeltaTime();
        flowController.tickStatus(dt);
        syncBackgroundMusicForState();

        appUiRenderer.captureClick(touchPos);

        switch (flowController.getState()) {
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
            	flowController.goToMainMenu();
                break;
            default:
                break;
        }
    }

    private void renderHowToPlay() {
        AppUiRenderer.HowToPlayAction action = appUiRenderer.renderHowToPlay(flowController.isRulesOpenedFromPause(), flowController.isRulesOpenedFromStart());
        
        if (action != AppUiRenderer.HowToPlayAction.NONE) {
            playButtonClick();
        }
        
        switch (action) {
            case BACK_TO_PAUSE:
            	flowController.setRulesOpenedFromPause(false);
                flowController.setRulesOpenedFromStart(false);
                flowController.startPlaying();
                break;
            case BACK_TO_MENU:
            	flowController.setRulesOpenedFromPause(false);
                flowController.setRulesOpenedFromStart(false);
                flowController.goToMainMenu();

                break;
            case CONTINUE_TO_AVATAR:
            	flowController.setRulesOpenedFromPause(false);
                flowController.setRulesOpenedFromStart(false);
                flowController.goToAvatarSetup();
                break;
            default:
                break;
        }
    }

    private void renderGameplay(float dt) {
        appUiRenderer.applyFullScreenProjection();

        if (!tutorialCoordinator.isFinished()) {
        	if (GameplayLoopOrchestrator.togglePauseOnEsc(flowController.isPaused()) != flowController.isPaused()) {
        	    flowController.togglePaused();
        	}
        }

        if (!flowController.isPaused()&& !tutorialCoordinator.isFinished()) {
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
                flowController.isPaused(),
                movementManager,
                sceneManager,
                entityManager,
                collisionManager,
                gameplayRuntime,
                activeConfig,
                gameSession);
        if (timerExpired && !tutorialCoordinator.isActive()) {
        	flowController.resume();
        	flowController.goToLeaderboardEntry();
            flowController.setLeaderboardNameEditing(true);
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
            flowController.togglePaused();
        }

        if (tutorialCoordinator.isActive() && tutorialCoordinator.isFinished()) {
        	flowController.pause();
            Rectangle continueButton = tutorialUiRenderer.createContinueButton();

            if (appUiRenderer.consumeClick(continueButton)) {
                playButtonClick();
                tutorialCoordinator.stop();
                flowController.resume();
                flowController.goToAvatarSetup();
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

        if (flowController.isPaused()) {
            GameplayLoopOrchestrator.PauseAction action =
                    GameplayLoopOrchestrator.renderPauseMenu(appUiRenderer, batch, font);
            switch (action) {
                case RESUME -> {
                    playButtonClick();
                    flowController.resume();
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
                    flowController.openRulesFromPause();
                }
                case QUIT_TO_MENU -> {
                    playButtonClick();
                    tutorialCoordinator.stop();
                    flowController.goToMainMenu();
                    flowController.resume();
                }
                default -> {
                }
            }
        }
    }

    private void renderLeaderboardEntry() {
        LeaderboardFlowOrchestrator.NameEditState updated =
                LeaderboardFlowOrchestrator.updateNameTyping(flowController.getState(), flowController.isLeaderboardNameEditing(), playerNameInput);
        playerNameInput = updated.playerNameInput();
        flowController.setLeaderboardNameEditing(updated.leaderboardNameEditing());
        if (updated.confirmed()) {
            showStatus("Name confirmed", 2f);
        } else if (updated.canceled()) {
            showStatus("Name edit cancelled", 2f);
        }
        AppUiRenderer.LeaderboardEntryAction action = appUiRenderer.renderLeaderboardEntry(
                gameSession.getScore(),
                selectedAvatarTexture,
                playerNameInput,
                flowController.isLeaderboardNameEditing());
        if (action != AppUiRenderer.LeaderboardEntryAction.NONE) {
            playButtonClick();
        }
        switch (action) {
            case ENABLE_NAME_EDIT:
            	flowController.setLeaderboardNameEditing(true);
                showStatus("Typing enabled. Press Enter to confirm name.", 2.5f);
                break;
            case REQUEST_UPLOAD:
            	flowController.setLeaderboardNameEditing(false);
                requestImageUpload("leaderboard-entry");
                break;
            case SUBMIT:
            	flowController.setLeaderboardNameEditing(false);
                submitLeaderboardEntry();
                break;
            case BACK_TO_MENU:
            	flowController.setLeaderboardNameEditing(false);
                flowController.goToMainMenu();
                break;
            default:
                break;
        }
    }

    private void renderLeaderboardView() {
        AppUiRenderer.LeaderboardViewAction action =
                appUiRenderer.renderLeaderboardView(leaderboardEntries, flowController.isLeaderboardOpenedFromMenu());
        if (action == AppUiRenderer.LeaderboardViewAction.FOOTER_CLICKED) {
            playButtonClick();
            flowController.goToMainMenu();
            playerNameInput = "";
            flowController.setLeaderboardNameEditing(false);
            flowController.setLeaderboardOpenedFromMenu(false);
            showStatus("Setup ready for next run", 2f);
        }
    }

    private void renderCredits() {
        AppUiRenderer.CreditsAction action = appUiRenderer.renderCredits();
        if (action == AppUiRenderer.CreditsAction.BACK_TO_MENU) {
            playButtonClick();
            flowController.closeCredits();
        }
    }

    private void startNewGame() {
        tutorialCoordinator.stop();
        gameSession = GameSession.fromConfig(difficultyConfig);
        flowController.resume();
        flowController.setLeaderboardNameEditing(false);
        playerNameInput = "";
        gameplayRuntime.startNewGame(gameSession, difficultyConfig, selectedAvatarTexture);
        flowController.startPlaying();
        showStatus("Game started (" + difficultyPreset.getLabel() + ")", 2f);
    }

    private void startTutorial() {
        DifficultyConfig tutorialConfig = tutorialCoordinator.getConfig();
        tutorialCoordinator.start();
        gameSession = GameSession.fromConfig(tutorialConfig);
        flowController.resume();
        flowController.setLeaderboardNameEditing(false);
        playerNameInput = "";
        gameplayRuntime.startNewGame(gameSession, tutorialConfig, selectedAvatarTexture);
        flowController.startPlaying();
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
        flowController.openLeaderboard(false);
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
            if (flowController.getState() != GameState.LEADERBOARD_ENTRY) {
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
            if (flowController.getState() != GameState.LEADERBOARD_ENTRY) {
                return;
            }
            String reason = event.getPayloadOrNull(String.class);
            if (reason == null || reason.isBlank()) {
                reason = "image selection failed";
            }
            showStatus("Upload failed: " + reason, 3f);
        });
    }


    private void showStatus(String message, float seconds) {
        flowController.showStatus(message, seconds);
    }

    private void playButtonClick() {
        StateFlowOrchestrator.playButtonClick(ioManager);
    }

    private void syncBackgroundMusicForState() {
        activeBackgroundTrack = StateFlowOrchestrator.syncBackgroundMusicForState(ioManager, flowController.getState(), activeBackgroundTrack);
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
            	flowController.pause();
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
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }
}
