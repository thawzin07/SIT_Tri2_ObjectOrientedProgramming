package com.sit.inf1009.project.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class StartMenuScene extends Scene implements IOListener {

    public interface ActionListener {
        void onStart();
        void onDifficulty();
        void onHowToPlay();
        void onHighScores();
    }

    private static final float VIRTUAL_W = 800f;
    private static final float VIRTUAL_H = 600f;

    private Texture backgroundTexture;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ButtonRenderer buttonRenderer;
    private MenuInputHandler inputHandler;
    private MenuButton[] buttons;

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
        buttonRenderer = new ButtonRenderer();

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_W, VIRTUAL_H, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_W / 2f, VIRTUAL_H / 2f, 0);
        camera.update();

        if (ioManager != null) {
            inputHandler = new MenuInputHandler(ioManager);
            ioManager.addListener(IOEvent.Type.DISPLAY_SHOW_HUD, this);
        } else {
            inputHandler = new MenuInputHandler(new InputOutputManager());
        }

        setupButtons();
    }

    private void setupButtons() {
        float btnW = VIRTUAL_W * 0.46f;
        float btnH = VIRTUAL_H * 0.10f;
        float btnX = (VIRTUAL_W - btnW) / 2f;
        float gap = VIRTUAL_H * 0.025f;
        float topY = VIRTUAL_H * 0.44f;

        buttons = new MenuButton[] {
            new MenuButton("START",
                btnX, topY, btnW, btnH,
                new Color(0.15f, 0.65f, 0.05f, 1f),
                new Color(0.30f, 0.85f, 0.15f, 1f),
                new Color(0.05f, 0.40f, 0.01f, 1f),
                new Color(0.08f, 0.45f, 0.02f, 1f)),

            new MenuButton("DIFFICULTY SETTINGS",
                btnX, topY - (btnH + gap), btnW, btnH,
                new Color(0.08f, 0.38f, 0.82f, 1f),
                new Color(0.20f, 0.58f, 0.98f, 1f),
                new Color(0.02f, 0.18f, 0.55f, 1f),
                new Color(0.04f, 0.22f, 0.60f, 1f)),

            new MenuButton("TUTORIAL",
                btnX, topY - (btnH + gap) * 2, btnW, btnH,
                new Color(0.82f, 0.52f, 0.02f, 1f),
                new Color(0.98f, 0.72f, 0.10f, 1f),
                new Color(0.55f, 0.30f, 0.01f, 1f),
                new Color(0.60f, 0.35f, 0.01f, 1f)),

            new MenuButton("HIGH SCORES",
                btnX, topY - (btnH + gap) * 3, btnW, btnH,
                new Color(0.62f, 0.28f, 0.02f, 1f),
                new Color(0.82f, 0.48f, 0.08f, 1f),
                new Color(0.38f, 0.14f, 0.01f, 1f),
                new Color(0.42f, 0.16f, 0.01f, 1f))
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
        buttonRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, VIRTUAL_W, VIRTUAL_H);
        batch.end();

        inputHandler.update(buttons, camera, viewport);
        int active = inputHandler.getActiveBtn();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < buttons.length; i++) {
            buttonRenderer.drawButton(buttons[i], active == i);
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);

        for (int i = 0; i < buttons.length; i++) {
            buttonRenderer.drawLabel(buttons[i], active == i);
        }

        if (inputHandler.wasJustTouched()) {
            handleButtonClick(active);
        }
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
        if (buttonRenderer != null) buttonRenderer.dispose();
        if (inputHandler != null) inputHandler.detach();
        if (ioManager != null)
            ioManager.removeListener(IOEvent.Type.DISPLAY_SHOW_HUD, this);
    }
}
