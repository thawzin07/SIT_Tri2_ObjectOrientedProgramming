package com.sit.project.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.entities.Entity;

public final class TextureRenderer implements SpriteComponent {
    private final float width;
    private final float height;

    public TextureRenderer(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Entity e, SpriteBatch batch) {
        Texture tex = e.getTexture();
        if (tex == null) return;

        batch.draw(tex, (float)e.getX(), (float)e.getY(), width, height);
    }
}
