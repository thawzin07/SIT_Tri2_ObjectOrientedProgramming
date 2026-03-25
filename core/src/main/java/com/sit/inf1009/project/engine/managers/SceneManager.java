package com.sit.inf1009.project.engine.managers;

import java.util.Stack;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.entities.Entity;

public class SceneManager {
    private Stack<Scene> scenes;

    public SceneManager() {
        this.scenes = new Stack<>();
    }

    public void push(Scene scene) {
        scenes.push(scene);
    }

    public void pop() {
        if (!scenes.isEmpty()) {
            scenes.pop();
        }
    }

    public void setScene(Scene scene) {
        pop();
        push(scene);
    }

    public String getCurrentSceneName() {
        return scenes.isEmpty() ? "None" : scenes.peek().getName();
    }

    public void update(float dt, List<Entity> entities) {
        if (!scenes.isEmpty()) {
            scenes.peek().update(dt, entities);
        }
    }

    public void render(SpriteBatch batch) {
        if (!scenes.isEmpty()) {
            ScreenUtils.clear(scenes.peek().getBackgroundColor());
        }
    }
}