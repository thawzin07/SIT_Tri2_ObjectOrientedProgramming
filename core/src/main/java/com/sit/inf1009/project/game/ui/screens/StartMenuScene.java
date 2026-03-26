package com.sit.inf1009.project.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class StartMenuScene extends Scene implements IOListener {

    private static final class MenuButtonData {
        private final String label;
        private final Color baseColor;
        private final Color lightColor;
        private final Color outlineColor;
        private final Color pressedColor;
        private final Rectangle bounds;

        private MenuButtonData(String label,
                               float x,
                               float y,
                               float w,
                               float h,
                               Color baseColor,
                               Color lightColor,
                               Color outlineColor,
                               Color pressedColor) {
            this.label = label;
            this.baseColor = baseColor;
            this.lightColor = lightColor;
            this.outlineColor = outlineColor;
            this.pressedColor = pressedColor;
            this.bounds = new Rectangle(x, y, w, h);
        }
    }

    public interface ActionListener {
        void onStart();
        void onDifficulty();
        void onHowToPlay();
        void onHighScores();
        void onCredits();
    }

    private static final float VIRTUAL_W = 800f;
    private static final float VIRTUAL_H = 600f;

    private Texture backgroundTexture;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer buttonShapeRenderer;
    private SpriteBatch buttonLabelBatch;
    private BitmapFont buttonFont;
    private GlyphLayout buttonLabelLayout;
    private MenuButtonData[] buttons;
    private int activeButtonIndex = -1;

    private InputOutputManager ioManager;
    private ActionListener actionListener;

    public StartMenuScene() {
        super("Start Menu", Color.BLACK);
    }

    public StartMenuScene(InputOutputManager ioManager) {
        super("Start Menu", Color.BLACK);
        this.ioManager = ioManager;
    }

    public StartMenuScene(InputOutputManager ioManager, ActionListener actionListener) {
        super("Start Menu", Color.BLACK);
        this.ioManager = ioManager;
        this.actionListener = actionListener;
    }

    @Override
    public void create() {
        FileHandle jpgBackground = Gdx.files.internal("start_menu_background.jpg");
        if (jpgBackground.exists()) {
            backgroundTexture = new Texture(jpgBackground);
        } else {
            backgroundTexture = new Texture("start_menu_background.png");
        }
        buttonShapeRenderer = new ShapeRenderer();
        buttonLabelBatch = new SpriteBatch();
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.6f);
        buttonLabelLayout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_W, VIRTUAL_H, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_W / 2f, VIRTUAL_H / 2f, 0);
        camera.update();

        if (ioManager != null) {
            ioManager.addListener(IOEvent.Type.DISPLAY_SHOW_HUD, this);
        }

        setupButtons();
    }

    private void setupButtons() {
        float btnW = VIRTUAL_W * 0.44f;
        float btnH = VIRTUAL_H * 0.085f;
        float btnX = (VIRTUAL_W - btnW) / 2f;
        float gap = VIRTUAL_H * 0.02f;
        float topY = VIRTUAL_H * 0.47f;

        buttons = new MenuButtonData[] {
            new MenuButtonData("START",
                btnX, topY, btnW, btnH,
                new Color(0.15f, 0.65f, 0.05f, 1f),
                new Color(0.30f, 0.85f, 0.15f, 1f),
                new Color(0.05f, 0.40f, 0.01f, 1f),
                new Color(0.08f, 0.45f, 0.02f, 1f)),

            new MenuButtonData("SETTINGS",
                btnX, topY - (btnH + gap), btnW, btnH,
                new Color(0.08f, 0.38f, 0.82f, 1f),
                new Color(0.20f, 0.58f, 0.98f, 1f),
                new Color(0.02f, 0.18f, 0.55f, 1f),
                new Color(0.04f, 0.22f, 0.60f, 1f)),

            new MenuButtonData("TUTORIAL",
                btnX, topY - (btnH + gap) * 2, btnW, btnH,
                new Color(0.82f, 0.52f, 0.02f, 1f),
                new Color(0.98f, 0.72f, 0.10f, 1f),
                new Color(0.55f, 0.30f, 0.01f, 1f),
                new Color(0.60f, 0.35f, 0.01f, 1f)),

            new MenuButtonData("HIGH SCORES",
                btnX, topY - (btnH + gap) * 3, btnW, btnH,
                new Color(0.62f, 0.28f, 0.02f, 1f),
                new Color(0.82f, 0.48f, 0.08f, 1f),
                new Color(0.38f, 0.14f, 0.01f, 1f),
                new Color(0.42f, 0.16f, 0.01f, 1f)),

            new MenuButtonData("CREDITS",
                btnX, topY - (btnH + gap) * 4, btnW, btnH,
                new Color(0.34f, 0.25f, 0.60f, 1f),
                new Color(0.50f, 0.39f, 0.78f, 1f),
                new Color(0.19f, 0.13f, 0.36f, 1f),
                new Color(0.24f, 0.18f, 0.44f, 1f))
        };
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(VIRTUAL_W / 2f, VIRTUAL_H / 2f, 0);
        camera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        buttonShapeRenderer.setProjectionMatrix(camera.combined);
        buttonLabelBatch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, VIRTUAL_W, VIRTUAL_H);
        batch.end();

        updateMenuInput();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < buttons.length; i++) {
            drawButton(buttons[i], activeButtonIndex == i);
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);

        for (int i = 0; i < buttons.length; i++) {
            drawLabel(buttons[i], activeButtonIndex == i);
        }

        if (Gdx.input.justTouched()) {
            handleButtonClick(activeButtonIndex);
        }
    }

    private void updateMenuInput() {
        if (!Gdx.input.isTouched()) {
            activeButtonIndex = -1;
            return;
        }

        Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touch,
                viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight());

        activeButtonIndex = -1;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].bounds.contains(touch.x, touch.y)) {
                activeButtonIndex = i;
                if (ioManager != null) {
                    ioManager.handleEvent(new IOEvent(IOEvent.Type.MOUSE_PRESSED, "btn:" + i));
                }
                break;
            }
        }
    }

    private void drawButton(MenuButtonData button, boolean pressed) {
        float x = button.bounds.x;
        float y = pressed ? button.bounds.y - 4 : button.bounds.y;
        float w = button.bounds.width;
        float h = button.bounds.height;
        float r = h / 2f;

        if (!pressed) {
            buttonShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            buttonShapeRenderer.setColor(0f, 0f, 0f, 0.35f);
            drawRoundedRect(x + 4, y - 7, w, h, r);
            buttonShapeRenderer.end();
        }

        buttonShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        buttonShapeRenderer.setColor(button.outlineColor);
        drawRoundedRect(x - 3, y - 3, w + 6, h + 6, r + 3);
        buttonShapeRenderer.end();

        buttonShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        buttonShapeRenderer.setColor(pressed ? button.pressedColor : button.baseColor);
        drawRoundedRect(x, y, w, h, r);
        buttonShapeRenderer.end();

        buttonShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        buttonShapeRenderer.setColor(button.lightColor);
        drawRoundedRect(x + 2, y + h * 0.45f, w - 4, h * 0.50f, r * 0.85f);
        buttonShapeRenderer.end();

        buttonShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        buttonShapeRenderer.setColor(1f, 1f, 1f, pressed ? 0.05f : 0.22f);
        drawRoundedRect(x + 6, y + h * 0.72f, w - 12, h * 0.20f, r * 0.6f);
        buttonShapeRenderer.end();
    }

    private void drawLabel(MenuButtonData button, boolean pressed) {
        float y = pressed ? button.bounds.y - 4 : button.bounds.y;
        buttonLabelLayout.setText(buttonFont, button.label);
        float textX = button.bounds.x + (button.bounds.width - buttonLabelLayout.width) / 2f;
        float textY = y + (button.bounds.height + buttonLabelLayout.height) / 2f;

        buttonLabelBatch.begin();
        buttonFont.setColor(0f, 0f, 0f, 0.85f);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    buttonFont.draw(buttonLabelBatch, button.label, textX + dx, textY + dy);
                }
            }
        }

        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(buttonLabelBatch, button.label, textX, textY);
        buttonLabelBatch.end();
    }

    private void drawRoundedRect(float x, float y, float w, float h, float r) {
        float radius = Math.min(r, Math.min(w / 2f, h / 2f));
        int segments = 20;
        buttonShapeRenderer.rect(x + radius, y, w - 2 * radius, h);
        buttonShapeRenderer.rect(x, y + radius, radius, h - 2 * radius);
        buttonShapeRenderer.rect(x + w - radius, y + radius, radius, h - 2 * radius);
        buttonShapeRenderer.arc(x + radius, y + radius, radius, 180, 90, segments);
        buttonShapeRenderer.arc(x + w - radius, y + radius, radius, 270, 90, segments);
        buttonShapeRenderer.arc(x + w - radius, y + h - radius, radius, 0, 90, segments);
        buttonShapeRenderer.arc(x + radius, y + h - radius, radius, 90, 90, segments);
    }

    private void handleButtonClick(int btnIndex) {
        if (actionListener != null) {
            switch (btnIndex) {
                case 0:
                    playButtonClick();
                    actionListener.onStart();
                    break;
                case 1:
                    playButtonClick();
                    actionListener.onDifficulty();
                    break;
                case 2:
                    playButtonClick();
                    actionListener.onHowToPlay();
                    break;
                case 3:
                    playButtonClick();
                    actionListener.onHighScores();
                    break;
                case 4:
                    playButtonClick();
                    actionListener.onCredits();
                    break;
                default:
                    break;
            }
            return;
        }

        switch (btnIndex) {
            case 0:
                playButtonClick();
                break;
            case 1:
                playButtonClick();
                if (ioManager != null)
                    ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "Difficulty - coming soon!"));
                break;
            case 2:
                playButtonClick();
                if (ioManager != null)
                    ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "How To Play - coming soon!"));
                break;
            case 3:
                playButtonClick();
                if (ioManager != null)
                    ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "High Scores - coming soon!"));
                break;
            case 4:
                playButtonClick();
                if (ioManager != null)
                    ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "Credits - coming soon!"));
                break;
            default:
                break;
        }
    }

    private void playButtonClick() {
        if (ioManager != null) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));
        }
    }

    @Override
    public void onIOEvent(IOEvent event) {
        if (event.getType() == IOEvent.Type.DISPLAY_SHOW_HUD) {
            System.out.println("[StartMenuScene] HUD: " + event.getPayload());
        }
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (buttonShapeRenderer != null) buttonShapeRenderer.dispose();
        if (buttonLabelBatch != null) buttonLabelBatch.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (ioManager != null)
            ioManager.removeListener(IOEvent.Type.DISPLAY_SHOW_HUD, this);
    }
}
