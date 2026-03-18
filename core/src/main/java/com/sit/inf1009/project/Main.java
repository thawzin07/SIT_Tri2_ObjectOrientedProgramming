package com.sit.inf1009.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
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
import java.util.Comparator;
import java.util.List;

public class Main extends ApplicationAdapter {

    private enum GameState {
        FOOD_MENU,
        AVATAR_SETUP,
        PLAYING,
        LEADERBOARD_ENTRY,
        LEADERBOARD_VIEW
    }

    private static class LeaderboardEntry {
        final String name;
        final int score;
        final Texture avatarTexture;
        final boolean ownsTexture;

        LeaderboardEntry(String name, int score, Texture avatarTexture, boolean ownsTexture) {
            this.name = name;
            this.score = score;
            this.avatarTexture = avatarTexture;
            this.ownsTexture = ownsTexture;
        }
    }

    private static final int PLAYER_ID = 1;
    private static final float BUTTON_W = 250f;
    private static final float BUTTON_H = 38f;

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

    private GameState gameState;
    private boolean paused;

    private Texture[] presetAvatars;
    private String[] presetAvatarLabels;
    private int selectedPresetIndex = -1;
    private Texture uploadedAvatarTexture;
    private String uploadedAvatarPath;
    private Texture selectedAvatarTexture;
    private boolean selectedAvatarIsUploaded;

    private final List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    private String playerNameInput = "";
    private String statusMessage = "";
    private float statusSecondsLeft = 0f;

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
        sceneManager = new SceneManager(entityManager, movementManager, collisionManager);
        gameSession = new GameSession(60f);
        paused = false;

        ioManager.registerInputHandler(new KeyboardInputHandler(ioManager));
        ioManager.registerInputHandler(new LibGdxMouseInputHandler(ioManager));
        new PlayerImageInputService(ioManager);
        ioManager.registerOutputHandler(new SoundOutputHandler());

        presetAvatarLabels = new String[] { "Droplet", "Bucket", "LibGDX" };
        presetAvatars = new Texture[] {
                new Texture(Gdx.files.internal("droplet.png")),
                new Texture(Gdx.files.internal("bucket.png")),
                new Texture(Gdx.files.internal("libgdx.png"))
        };

        foodMenuScene = new StartMenuScene(ioManager, new StartMenuScene.ActionListener() {
            @Override
            public void onStart() {
                gameState = GameState.AVATAR_SETUP;
            }

            @Override
            public void onDifficulty() {
                showStatus("Difficulty - coming soon!", 2f);
            }

            @Override
            public void onHowToPlay() {
                showStatus("How To Play - coming soon!", 2f);
            }

            @Override
            public void onHighScores() {
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

    private void renderGameplay(float dt) {
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
            shapeRenderer.setColor(Color.WHITE);
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
        font.draw(batch, "Enter: Submit plate | R: Reset plate", 20f, Gdx.graphics.getHeight() - 56f);
        font.draw(batch, "Timer: " + (int) Math.ceil(gameSession.getTimer()), 20f, Gdx.graphics.getHeight() - 74f);
        font.draw(batch, "Score: " + gameSession.getScore(), 20f, Gdx.graphics.getHeight() - 92f);
        font.draw(batch, "Plate V/P/C/O: " + gameSession.getVegetableCount() + "/"
                + gameSession.getProteinCount() + "/" + gameSession.getCarbCount() + "/"
                + gameSession.getOilCount(), 20f, Gdx.graphics.getHeight() - 110f);

        if (paused) {
            font.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f - 24f, Gdx.graphics.getHeight() / 2f);
        }
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void renderLeaderboardEntry() {
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
        float centerX = Gdx.graphics.getWidth() / 2f;
        float topY = Gdx.graphics.getHeight() - 60f;
        Rectangle playAgainButton = new Rectangle(centerX - (BUTTON_W / 2f), 30f, BUTTON_W, BUTTON_H);
        if (consumeClick(playAgainButton)) {
            gameState = GameState.FOOD_MENU;
            playerNameInput = "";
            showStatus("Setup ready for next run", 2f);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonRect(playAgainButton);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Leaderboard", centerX - 45f, topY);

        int maxRows = Math.min(5, leaderboardEntries.size());
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

        font.draw(batch, "Play Again", playAgainButton.x + 84f, playAgainButton.y + 24f);
        drawStatus(batch, 20f, 24f);
        batch.end();
    }

    private void startNewGame() {
        gameSession = new GameSession(60f);
        paused = false;
        playerNameInput = "";
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
        loadEntitiesForLevel(1);
        gameState = GameState.PLAYING;
        showStatus("Game started", 2f);
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
        int npcCount = (levelNum == 1) ? 8 : 4;
        for (int i = 0; i < npcCount; i++) {
            Entity npc = new Entity(100 + i);
            npc.setXPosition(100 + rng.nextInt(500));
            npc.setYPosition(100 + rng.nextInt(300));
            int dirX = rng.nextBoolean() ? 1 : -1;
            int dirY = rng.nextBoolean() ? 1 : -1;
            npc.setMovement(new AIMovement(120, dirX, dirY));
            npc.setCollidable(new FoodCollidableComponent(8, FoodCategory.VEGETABLE, 1, gameSession));
            sceneManager.spawnEntity(npc);
        }
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
        selectedPresetIndex = index;
        selectedAvatarIsUploaded = false;
        selectedAvatarTexture = presetAvatars[index];
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

        leaderboardEntries.add(new LeaderboardEntry(playerNameInput, gameSession.getScore(), entryTexture, ownsTexture));
        leaderboardEntries.sort(Comparator.comparingInt((LeaderboardEntry e) -> e.score).reversed());
        gameState = GameState.LEADERBOARD_VIEW;
        showStatus("Leaderboard entry submitted", 2f);
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
        gameSession.submitPlate();
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
        if (foodMenuScene != null) {
            foodMenuScene.dispose();
        }
        if (avatarSetupScreen != null) {
            avatarSetupScreen.dispose();
        }
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        ioManager.shutdown();
    }
}
