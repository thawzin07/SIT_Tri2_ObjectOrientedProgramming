package com.sit.inf1009.project.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public final class UiPanelRenderer {

    private UiPanelRenderer() {
    }

    public static void drawScreenPanel(ShapeRenderer shapeRenderer, Rectangle panel) {
        drawScreenPanel(shapeRenderer, panel, 1f);
    }

    public static void drawScreenPanel(ShapeRenderer shapeRenderer, Rectangle panel, float scale) {
        float s = Math.max(0.5f, scale);
        float outerInset = 2f * s;
        float innerInset = 6f * s;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.02f, 0.03f, 0.06f, 0.9f);
        shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
        shapeRenderer.setColor(0.08f, 0.1f, 0.16f, 0.92f);
        shapeRenderer.rect(panel.x + innerInset, panel.y + innerInset, panel.width - (innerInset * 2f), panel.height - (innerInset * 2f));
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.72f, 0.78f, 0.9f, 0.7f);
        shapeRenderer.rect(panel.x + outerInset, panel.y + outerInset, panel.width - (outerInset * 2f), panel.height - (outerInset * 2f));
        shapeRenderer.end();
    }

    public static void drawActionButton(ShapeRenderer shapeRenderer, Rectangle bounds, Color fillColor) {
        drawActionButton(shapeRenderer, bounds, fillColor, 1f);
    }

    public static void drawActionButton(ShapeRenderer shapeRenderer, Rectangle bounds, Color fillColor, float scale) {
        float s = Math.max(0.5f, scale);
        float edgeInset = 2f * s;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(fillColor.r, fillColor.g, fillColor.b, 0.92f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(
                Math.min(1f, fillColor.r + 0.12f),
                Math.min(1f, fillColor.g + 0.12f),
                Math.min(1f, fillColor.b + 0.12f),
                0.32f);
        shapeRenderer.rect(bounds.x + edgeInset, bounds.y + bounds.height * 0.52f, bounds.width - (edgeInset * 2f), bounds.height * 0.42f);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.9f, 0.92f, 1f, 0.85f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }

    public static void drawTextInputField(ShapeRenderer shapeRenderer, Rectangle bounds, boolean active) {
        drawTextInputField(shapeRenderer, bounds, active, 1f);
    }

    public static void drawTextInputField(ShapeRenderer shapeRenderer, Rectangle bounds, boolean active, float scale) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.04f, 0.05f, 0.08f, 0.96f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (active) {
            shapeRenderer.setColor(0.36f, 0.82f, 1f, 1f);
        } else {
            shapeRenderer.setColor(0.78f, 0.82f, 0.92f, 0.85f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }
}
