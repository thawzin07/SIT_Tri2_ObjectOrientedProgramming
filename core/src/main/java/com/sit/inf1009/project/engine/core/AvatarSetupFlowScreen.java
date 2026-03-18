package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class AvatarSetupFlowScreen implements IOListener {

    public static class SelectionResult {
        private final int presetIndex;
        private final String uploadedPath;

        public SelectionResult(int presetIndex, String uploadedPath) {
            this.presetIndex = presetIndex;
            this.uploadedPath = uploadedPath;
        }

        public int getPresetIndex() {
            return presetIndex;
        }

        public String getUploadedPath() {
            return uploadedPath;
        }

        public boolean isUploaded() {
            return uploadedPath != null && !uploadedPath.isBlank();
        }
    }

    public interface ActionListener {
        void onBackToMainMenu();
        void onStartGame(SelectionResult result);
    }

    private static final float VIRTUAL_W = 800f;
    private static final float VIRTUAL_H = 600f;

    private final InputOutputManager ioManager;
    private final Texture[] presetAvatars;
    private final String[] presetLabels;
    private final ActionListener actionListener;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private Rectangle[] avatarRects;
    private Rectangle uploadButton;
    private Rectangle startButton;
    private Rectangle backButton;

    private int selectedPresetIndex = 0;
    private String selectedUploadedPath;
    private Texture uploadedPreviewTexture;
    private String statusText = "";

    public AvatarSetupFlowScreen(InputOutputManager ioManager,
                                 Texture[] presetAvatars,
                                 String[] presetLabels,
                                 ActionListener actionListener) {
        this.ioManager = ioManager;
        this.presetAvatars = presetAvatars;
        this.presetLabels = presetLabels;
        this.actionListener = actionListener;
    }

    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_W, VIRTUAL_H, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.position.set(VIRTUAL_W / 2f, VIRTUAL_H / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        glyphLayout = new GlyphLayout();

        buildLayout();

        ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTED, this);
        ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, this);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    public void render(SpriteBatch batch) {
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBoxes();

        batch.begin();
        drawCentered(batch, "Player Setup", VIRTUAL_W / 2f, 470f);
        drawCentered(batch, "Choose a preset avatar or upload your own image", VIRTUAL_W / 2f, 440f);
        drawCentered(batch, "Gameplay starts after avatar selection", VIRTUAL_W / 2f, 420f);

        for (int i = 0; i < avatarRects.length; i++) {
            Rectangle rect = avatarRects[i];
            Texture texture = presetAvatars[i];
            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
            drawCentered(batch, presetLabels[i], rect.x + rect.width / 2f, rect.y - 8f);
        }

        if (uploadedPreviewTexture != null) {
            drawCentered(batch, "Uploaded Preview", VIRTUAL_W / 2f, 108f);
            batch.draw(uploadedPreviewTexture, (VIRTUAL_W / 2f) - 12f, 82f, 24f, 24f);
        }

        drawCentered(batch, "Upload Custom Image", uploadButton.x + uploadButton.width / 2f, uploadButton.y + 25f);
        drawCentered(batch, "Start Game", startButton.x + startButton.width / 2f, startButton.y + 25f);
        drawCentered(batch, "Back to Main Menu", backButton.x + backButton.width / 2f, backButton.y + 25f);

        if (statusText != null && !statusText.isBlank()) {
            drawCentered(batch, statusText, VIRTUAL_W / 2f, 26f);
        }

        batch.end();

        handleInput();
    }

    private void buildLayout() {
        float avatarSize = 82f;
        float gap = 12f;
        float totalW = (avatarRectsCount() * avatarSize) + ((avatarRectsCount() - 1) * gap);
        float startX = (VIRTUAL_W - totalW) / 2f;

        avatarRects = new Rectangle[avatarRectsCount()];
        for (int i = 0; i < avatarRects.length; i++) {
            avatarRects[i] = new Rectangle(startX + i * (avatarSize + gap), 300f, avatarSize, avatarSize);
        }

        uploadButton = new Rectangle((VIRTUAL_W - 320f) / 2f, 210f, 320f, 42f);
        startButton = new Rectangle((VIRTUAL_W - 320f) / 2f, 155f, 320f, 42f);
        backButton = new Rectangle((VIRTUAL_W - 320f) / 2f, 100f, 320f, 42f);
    }

    private int avatarRectsCount() {
        return Math.min(presetAvatars.length, presetLabels.length);
    }

    private void drawBoxes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.48f);
        for (Rectangle rect : avatarRects) {
            shapeRenderer.rect(rect.x - 3f, rect.y - 3f, rect.width + 6f, rect.height + 6f);
        }
        shapeRenderer.rect(uploadButton.x, uploadButton.y, uploadButton.width, uploadButton.height);
        shapeRenderer.rect(startButton.x, startButton.y, startButton.width, startButton.height);
        shapeRenderer.rect(backButton.x, backButton.y, backButton.width, backButton.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < avatarRects.length; i++) {
            if (i == selectedPresetIndex && selectedUploadedPath == null) {
                shapeRenderer.setColor(0.25f, 0.8f, 1f, 1f);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }
            Rectangle rect = avatarRects[i];
            shapeRenderer.rect(rect.x - 3f, rect.y - 3f, rect.width + 6f, rect.height + 6f);
        }
        shapeRenderer.end();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        Vector2 world = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        for (int i = 0; i < avatarRects.length; i++) {
            if (avatarRects[i].contains(world.x, world.y)) {
                selectedPresetIndex = i;
                selectedUploadedPath = null;
                disposeUploadedPreview();
                statusText = "Preset selected: " + presetLabels[i];
                return;
            }
        }

        if (uploadButton.contains(world.x, world.y)) {
            ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, "setup-screen"));
            return;
        }

        if (startButton.contains(world.x, world.y)) {
            boolean hasPreset = selectedPresetIndex >= 0 && selectedPresetIndex < avatarRects.length;
            boolean hasUpload = selectedUploadedPath != null && !selectedUploadedPath.isBlank();
            if (!hasPreset && !hasUpload) {
                statusText = "Please select/upload an avatar first";
                return;
            }
            if (actionListener != null) {
                actionListener.onStartGame(new SelectionResult(selectedPresetIndex, selectedUploadedPath));
            }
            return;
        }

        if (backButton.contains(world.x, world.y)) {
            if (actionListener != null) {
                actionListener.onBackToMainMenu();
            }
        }
    }

    @Override
    public void onIOEvent(IOEvent event) {
        if (event == null) return;

        if (event.getType() == IOEvent.Type.PLAYER_IMAGE_SELECTED) {
            String path = event.getPayloadOrNull(String.class);
            if (path == null || path.isBlank()) {
                statusText = "Upload failed";
                return;
            }
            try {
                Texture texture = new Texture(Gdx.files.absolute(path));
                disposeUploadedPreview();
                uploadedPreviewTexture = texture;
                selectedUploadedPath = path;
                selectedPresetIndex = -1;
                statusText = "Uploaded: " + Gdx.files.absolute(path).name();
            } catch (Exception e) {
                statusText = "Image load failed";
            }
        }

        if (event.getType() == IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED) {
            String reason = event.getPayloadOrNull(String.class);
            if (reason == null || reason.isBlank()) {
                statusText = "Upload cancelled";
            } else {
                statusText = "Upload failed: " + reason;
            }
        }
    }

    private void drawCentered(SpriteBatch batch, String text, float centerX, float baselineY) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width / 2f, baselineY);
    }

    public void dispose() {
        ioManager.removeListener(IOEvent.Type.PLAYER_IMAGE_SELECTED, this);
        ioManager.removeListener(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, this);
        disposeUploadedPreview();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }

    private void disposeUploadedPreview() {
        if (uploadedPreviewTexture != null) {
            uploadedPreviewTexture.dispose();
            uploadedPreviewTexture = null;
        }
    }
}
