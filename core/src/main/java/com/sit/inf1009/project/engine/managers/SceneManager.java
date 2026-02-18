package com.sit.inf1009.project.engine.managers;

import java.util.Stack;
import com.sit.inf1009.project.engine.core.Scene;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SceneManager {

    private Stack<Scene> scenes;

    public SceneManager() {
        this.scenes = new Stack<>();
    }

    public void push(Scene scene) {
        scenes.push(scene);
        System.out.println("SceneManager: Pushed scene -> " + scene.getName());
    }


    public void pop() {
        if (!scenes.isEmpty()) {
            Scene poppedScene = scenes.pop();
            poppedScene.dispose(); 
            System.out.println("SceneManager: Popped scene -> " + poppedScene.getName());
        }
    }


    public void set(Scene scene) {
        while (!scenes.isEmpty()) {
            scenes.pop().dispose();
        }
        push(scene);
    }

 
    public void update(float dt) {
        if (scenes.isEmpty()) return;

        scenes.peek().update(dt);
    }
    
    public int getSceneCount() {
    	return scenes.size();
    }


    public void render(SpriteBatch batch) {
        if (scenes.isEmpty()) return;


        scenes.peek().render(batch);
        
    }
}