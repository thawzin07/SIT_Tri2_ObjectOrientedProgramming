package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.sit.inf1009.project.engine.managers.SceneManager;

import com.sit.inf1009.project.Main;

public class PlayerSelectionScene extends Scene {

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Rectangle player1Btn;
    private Rectangle player2Btn;
    private Rectangle backBtn;

    public PlayerSelectionScene() {
        super("Player Selection", Color.DARK_GRAY);
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float btnW = 280f;
        float btnH = 65f;
        float btnX = (screenW - btnW) / 2f;

        player1Btn = new Rectangle(btnX, screenH * 0.55f, btnW, btnH);
        player2Btn = new Rectangle(btnX, screenH * 0.40f, btnW, btnH);
        backBtn    = new Rectangle(btnX, screenH * 0.20f, btnW, btnH);
    }

    @Override
    public void render(SpriteBatch batch) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        // Clear to a solid color background
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.7f, 0.2f, 0.9f));
        shapeRenderer.rect(player1Btn.x, player1Btn.y, player1Btn.width, player1Btn.height);
        shapeRenderer.setColor(new Color(0.2f, 0.4f, 0.8f, 0.9f));
        shapeRenderer.rect(player2Btn.x, player2Btn.y, player2Btn.width, player2Btn.height);
        shapeRenderer.setColor(new Color(0.6f, 0.1f, 0.1f, 0.9f));
        shapeRenderer.rect(backBtn.x, backBtn.y, backBtn.width, backBtn.height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw labels and title
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "SELECT PLAYER", w / 2f - 130, h * 0.80f);
        font.setColor(Color.WHITE);
        font.draw(batch, "PLAYER 1",  player1Btn.x + 60, player1Btn.y + 45);
        font.draw(batch, "PLAYER 2",  player2Btn.x + 60, player2Btn.y + 45);
        font.draw(batch, "< BACK",    backBtn.x + 80,    backBtn.y + 45);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (player1Btn.contains(touchX, touchY)) {
            System.out.println("Player 1 selected");

            GamePlayScene gps = new GamePlayScene();
            gps.initManagers(Main.em(), Main.mm(), Main.io());
            SceneManager.getInstance().setScene(gps);

        } else if (player2Btn.contains(touchX, touchY)) {
            System.out.println("Player 2 selected");

            GamePlayScene gps = new GamePlayScene();
            gps.initManagers(Main.em(), Main.mm(), Main.io());
            SceneManager.getInstance().setScene(gps);
        } else if (backBtn.contains(touchX, touchY)) {
            SceneManager.getInstance().setScene(new StartMenuScene()); // back to menu
        }
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}