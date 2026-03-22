package com.sit.inf1009.project.game.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ButtonRenderer {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch   uiBatch;
    private final BitmapFont    font;
    private final GlyphLayout   layout;

    public ButtonRenderer() {
        shapeRenderer = new ShapeRenderer();
        uiBatch       = new SpriteBatch();
        font          = new BitmapFont();
        font.getData().setScale(1.6f);
        layout        = new GlyphLayout();
    }

    public void setProjectionMatrix(com.badlogic.gdx.math.Matrix4 matrix) {
        shapeRenderer.setProjectionMatrix(matrix);
        uiBatch.setProjectionMatrix(matrix);
    }

    public void drawButton(MenuButton btn, boolean pressed) {
        float x = btn.bounds.x;
        float y = pressed ? btn.bounds.y - 4 : btn.bounds.y;
        float w = btn.bounds.width;
        float h = btn.bounds.height;
        float r = h / 2f;

        // Drop shadow
        if (!pressed) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
            drawRoundedRect(x + 4, y - 7, w, h, r);
            shapeRenderer.end();
        }

        // Outline
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(btn.outlineColor);
        drawRoundedRect(x - 3, y - 3, w + 6, h + 6, r + 3);
        shapeRenderer.end();

        // Base color
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(pressed ? btn.pressedColor : btn.baseColor);
        drawRoundedRect(x, y, w, h, r);
        shapeRenderer.end();

        // Light gradient top half
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(btn.lightColor);
        drawRoundedRect(x + 2, y + h * 0.45f, w - 4, h * 0.50f, r * 0.85f);
        shapeRenderer.end();

        // White sheen
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, pressed ? 0.05f : 0.22f);
        drawRoundedRect(x + 6, y + h * 0.72f, w - 12, h * 0.20f, r * 0.6f);
        shapeRenderer.end();
    }

    public void drawLabel(MenuButton btn, boolean pressed) {
        float y = pressed ? btn.bounds.y - 4 : btn.bounds.y;
        layout.setText(font, btn.label);
        float textX = btn.bounds.x + (btn.bounds.width - layout.width) / 2f;
        float textY = y + (btn.bounds.height + layout.height) / 2f;

        uiBatch.begin();

        // Fake bold — dark outline passes
        font.setColor(0f, 0f, 0f, 0.85f);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0)
                    font.draw(uiBatch, btn.label, textX + dx, textY + dy);
            }
        }

        // White text on top
        font.setColor(Color.WHITE);
        font.draw(uiBatch, btn.label, textX, textY);

        uiBatch.end();
    }

    private void drawRoundedRect(float x, float y, float w, float h, float r) {
        r = Math.min(r, Math.min(w / 2f, h / 2f));
        int seg = 20;
        shapeRenderer.rect(x + r, y, w - 2 * r, h);
        shapeRenderer.rect(x, y + r, r, h - 2 * r);
        shapeRenderer.rect(x + w - r, y + r, r, h - 2 * r);
        shapeRenderer.arc(x + r,     y + r,     r, 180, 90, seg);
        shapeRenderer.arc(x + w - r, y + r,     r, 270, 90, seg);
        shapeRenderer.arc(x + w - r, y + h - r, r, 0,   90, seg);
        shapeRenderer.arc(x + r,     y + h - r, r, 90,  90, seg);
    }

    public void dispose() {
        shapeRenderer.dispose();
        uiBatch.dispose();
        font.dispose();
    }
}
