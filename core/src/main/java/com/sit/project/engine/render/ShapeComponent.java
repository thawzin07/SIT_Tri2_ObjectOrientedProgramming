package com.sit.project.engine.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sit.inf1009.project.engine.entities.Entity;

public interface ShapeComponent extends RenderComponent {

    @Override
    default RenderPass pass() {
        return RenderPass.SHAPE;
    }

    void draw(Entity e, ShapeRenderer sr);
}