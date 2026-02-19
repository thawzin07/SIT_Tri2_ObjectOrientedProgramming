package com.sit.project.engine.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sit.inf1009.project.engine.entities.Entity;

public final class ShapeRenderClass implements ShapeComponent {

	private final ShapeType type;

    private final float a, b, c, d; 

    private ShapeRenderClass(ShapeType type, float a, float b, float c, float d) {
        this.type = type;
        this.a = a; this.b = b; this.c = c; this.d = d;
    }

    public static ShapeRenderClass circle(float radius) {
        return new ShapeRenderClass(ShapeType.CIRCLE, radius, 0, 0, 0);
    }

    public static ShapeRenderClass rect(float width, float height) {
        return new ShapeRenderClass(ShapeType.RECT, width, height, 0, 0);
    }

    public static ShapeRenderClass triangle(float x2, float y2, float x3, float y3) {
        return new ShapeRenderClass(ShapeType.TRIANGLE, x2, y2, x3, y3);
    }

    public static ShapeRenderClass line(float x2, float y2) {
        return new ShapeRenderClass(ShapeType.LINE, x2, y2, 0, 0);
    }

    @Override
    public void draw(Entity e, ShapeRenderer sr) {
        float x = (float) e.getX();
        float y = (float) e.getY();

        switch (type) {
            case CIRCLE -> sr.circle(x, y, a);
            case RECT -> sr.rect(x, y, a, b);
            case TRIANGLE -> sr.triangle(x, y, x + a, y + b, x + c, y + d);
            case LINE -> sr.line(x, y, x + a, y + b);
        }
    }
}