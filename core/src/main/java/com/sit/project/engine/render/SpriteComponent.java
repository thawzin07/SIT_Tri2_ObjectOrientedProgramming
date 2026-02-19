package com.sit.project.engine.render;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.entities.Entity;

public interface SpriteComponent extends RenderComponent {

    @Override
    default RenderPass pass() {
        return RenderPass.SPRITE;
    }

    void draw(Entity e, SpriteBatch batch);
}
