package com.sit.inf1009.project.game.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.sit.inf1009.project.engine.entities.Entity;

public class MenuButton extends Entity {

    public final String label;
    public final Color baseColor;
    public final Color lightColor;
    public final Color outlineColor;
    public final Color pressedColor;
    public final Rectangle bounds;

    private static int nextId = 1000;

    public MenuButton(String label, float x, float y, float w, float h,
                      Color baseColor, Color lightColor,
                      Color outlineColor, Color pressedColor) {
        super(nextId++);
        this.label        = label;
        this.baseColor    = baseColor;
        this.lightColor   = lightColor;
        this.outlineColor = outlineColor;
        this.pressedColor = pressedColor;
        this.bounds       = new Rectangle(x, y, w, h);
        setXPosition(x);
        setYPosition(y);
    }
}
