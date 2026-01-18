package com.sit.inf1009.project.engine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

//Declaration : Not yet implemented any method logic her 
//Just put there what i can think of for now as a skeleton

/**
 * InputOutputManager Class
 * 
 * This class manages all user input (keyboard, mouse) and output (text rendering).
 * It is part of the Abstract Engine and provides reusable input/output functionality
 * for any simulation without containing game-specific logic.
 * 
 * Uses Singleton pattern to ensure only one instance exists throughout the application.
 * Implements InputProcessor to handle libGDX input events.
 * 
 *@author INF1009 ThawZin Htun

 */
public class InputOutputManager implements InputProcessor {
    
    // Singleton pattern - only one InputOutputManager should exist
    private static InputOutputManager instance;
    
    // Input tracking
    private Map<Integer, Boolean> keysPressed;
    private Map<Integer, Boolean> keysJustPressed;
    private Vector2 mousePosition;
    private boolean mouseLeftPressed;
    private boolean mouseRightPressed;
    private boolean mouseLeftJustPressed;
    private boolean mouseRightJustPressed;
    
    // Output rendering
    private SpriteBatch batch;
    private BitmapFont font;
    
    /**
     * Private Constructor
     * 
     * Initializes all input tracking data structures and output rendering components.
     * Private to enforce singleton pattern - use getInstance() instead.
     */
    private InputOutputManager() {
        keysPressed = new HashMap<>();
        keysJustPressed = new HashMap<>();
        mousePosition = new Vector2();
        mouseLeftPressed = false;
        mouseRightPressed = false;
        mouseLeftJustPressed = false;
        mouseRightJustPressed = false;
        
        // Initialize rendering components
        batch = new SpriteBatch();
        font = new BitmapFont();
    }
    
    /**
     * Get Singleton Instance
     * 
     * Returns the single instance of InputOutputManager. Creates it if it doesn't exist.
     * 
     * @return The singleton instance of InputOutputManager
     */
    public static InputOutputManager getInstance() {
        if (instance == null) {
            instance = new InputOutputManager();
        }
        return instance;
    }
    
    /**
     * Initialize Input Manager
     * 
     * Sets this manager as the input processor for libGDX.
     * Must be called in the game's create() method before using input functions.
     */
    public void initialize() {
        Gdx.input.setInputProcessor(this);
    }
    
    /**
     * Update Input Manager
     * 
     * Resets "just pressed" states for the next frame.
     * Must be called every frame in the game's render() method.
     */
    public void update() {
        // Clear "just pressed" states for next frame
        keysJustPressed.clear();
        mouseLeftJustPressed = false;
        mouseRightJustPressed = false;
    }
    
    // ==================== INPUT METHODS ====================
    
    /**
     * Check if Key is Pressed
     * 
     * Checks if a specific key is currently being held down.
     * 
     * @param keycode The key to check (use Input.Keys constants)
     * @return true if key is currently pressed, false otherwise
     */
    public boolean isKeyPressed(int keycode) {
        return keysPressed.getOrDefault(keycode, false);
    }
    
    /**
     * Check if Key was Just Pressed
     * 
     * Checks if a specific key was pressed during this frame only.
     * Useful for single-press actions like jumping or shooting.
     * 
     * @param keycode The key to check (use Input.Keys constants)
     * @return true if key was just pressed this frame, false otherwise
     */
    public boolean isKeyJustPressed(int keycode) {
        return keysJustPressed.getOrDefault(keycode, false);
    }
    
    /**
     * Get Mouse Position
     * 
     * Returns the current position of the mouse cursor on screen.
     * Returns a copy to prevent external modification of internal state.
     * 
     * @return Vector2 containing x and y coordinates of mouse position
     */
    public Vector2 getMousePosition() {
        return mousePosition.cpy();
    }
    
    /**
     * Check if Left Mouse Button is Pressed
     * 
     * Checks if the left mouse button is currently being held down.
     * 
     * @return true if left mouse button is pressed, false otherwise
     */
    public boolean isMouseLeftPressed() {
        return mouseLeftPressed;
    }
    
    /**
     * Check if Right Mouse Button is Pressed
     * 
     * Checks if the right mouse button is currently being held down.
     * 
     * @return true if right mouse button is pressed, false otherwise
     */
    public boolean isMouseRightPressed() {
        return mouseRightPressed;
    }
    
    /**
     * Check if Left Mouse Button was Just Pressed
     * 
     * Checks if the left mouse button was clicked during this frame only.
     * 
     * @return true if left mouse button was just pressed this frame, false otherwise
     */
    public boolean isMouseLeftJustPressed() {
        return mouseLeftJustPressed;
    }
    
    /**
     * Check if Right Mouse Button was Just Pressed
     * 
     * Checks if the right mouse button was clicked during this frame only.
     * 
     * @return true if right mouse button was just pressed this frame, false otherwise
     */
    public boolean isMouseRightJustPressed() {
        return mouseRightJustPressed;
    }
    
    // ==================== OUTPUT METHODS ====================
    
    /**
     * Render Text on Screen
     * 
     * Displays text at a specific position on the screen.
     * Coordinates are in screen space (pixels).
     * 
     * @param text The string text to display on screen
     * @param x The x-coordinate position (horizontal)
     * @param y The y-coordinate position (vertical, from bottom)
     */
    public void renderText(String text, double x, double y) {
        batch.begin();
        font.draw(batch, text, (float)x, (float)y);
        batch.end();
    }
    
    /**
     * Render Debug Information
     * 
     * Displays debug text at the top-left corner of the screen.
     * Useful for showing FPS, entity counts, or other debugging data.
     * 
     * @param debugInfo The debug information string to display
     */
    public void renderDebugInfo(String debugInfo) {
        renderText(debugInfo, 10.0, (double)Gdx.graphics.getHeight() - 10.0);
    }
    
    // ==================== INPUT PROCESSOR INTERFACE METHODS ====================
    
    /**
     * Key Down Event Handler
     * 
     * Called when a key is pressed down. Tracks the key as both pressed and just pressed.
     * Part of libGDX InputProcessor interface implementation.
     * 
     * @param keycode The code of the key that was pressed
     * @return true to indicate the event was handled
     */
    @Override
    public boolean keyDown(int keycode) {
        keysPressed.put(keycode, true);
        keysJustPressed.put(keycode, true);
        return true;
    }
    
    /**
     * Key Up Event Handler
     * 
     * Called when a key is released. Marks the key as no longer pressed.
     * Part of libGDX InputProcessor interface implementation.
     * 
     * @param keycode The code of the key that was released
     * @return true to indicate the event was handled
     */
    @Override
    public boolean keyUp(int keycode) {
        keysPressed.put(keycode, false);
        return true;
    }
    
    /**
     * Key Typed Event Handler
     * 
     * Called when a character key is typed (press and release).
     * Currently not used but required by InputProcessor interface.
     * 
     * @param character The character that was typed
     * @return false to indicate the event was not handled
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    /**
     * Touch Down Event Handler
     * 
     * Called when a mouse button is pressed or screen is touched.
     * Tracks mouse position and button states.
     * 
     * @param screenX The x-coordinate where the touch occurred
     * @param screenY The y-coordinate where the touch occurred
     * @param pointer The pointer index for multi-touch
     * @param button The button that was pressed
     * @return true to indicate the event was handled
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mousePosition.set(screenX, screenY);
        
        if (button == Input.Buttons.LEFT) {
            mouseLeftPressed = true;
            mouseLeftJustPressed = true;
        } else if (button == Input.Buttons.RIGHT) {
            mouseRightPressed = true;
            mouseRightJustPressed = true;
        }
        return true;
    }
    
    /**
     * Touch Up Event Handler
     * 
     * Called when a mouse button is released or touch ends.
     * Updates button press states accordingly.
     * 
     * @param screenX The x-coordinate where the release occurred
     * @param screenY The y-coordinate where the release occurred
     * @param pointer The pointer index for multi-touch
     * @param button The button that was released
     * @return true to indicate the event was handled
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            mouseLeftPressed = false;
        } else if (button == Input.Buttons.RIGHT) {
            mouseRightPressed = false;
        }
        return true;
    }
    
    /**
     * Touch Dragged Event Handler
     * 
     * Called when the mouse is moved while a button is held down.
     * Updates the current mouse position.
     * 
     * @param screenX The x-coordinate of the drag position
     * @param screenY The y-coordinate of the drag position
     * @param pointer The pointer index for multi-touch
     * @return true to indicate the event was handled
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mousePosition.set((float)screenX, (float)screenY);
        return true;
    }
    
    /**
     * Mouse Moved Event Handler
     * 
     * Called when the mouse cursor moves without any buttons pressed.
     * Updates the current mouse position.
     * 
     * @param screenX The x-coordinate of the mouse position
     * @param screenY The y-coordinate of the mouse position
     * @return true to indicate the event was handled
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set((float)screenX, (float)screenY);
        return true;
    }
    
    /**
     * Scroll Event Handler
     * 
     * Called when the mouse wheel is scrolled.
     * Currently not used but required by InputProcessor interface.
     * 
     * @param amountX The horizontal scroll amount
     * @param amountY The vertical scroll amount
     * @return false to indicate the event was not handled
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    /**
     * Touch Cancelled Event Handler
     * 
     * Called when a touch event is cancelled (e.g., when another application takes focus).
     * Resets all mouse button states to prevent stuck button issues.
     * 
     * @param screenX The x-coordinate where the cancellation occurred
     * @param screenY The y-coordinate where the cancellation occurred
     * @param pointer The pointer index for multi-touch
     * @param button The button that was cancelled
     * @return true to indicate the event was handled
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // Reset all button states when touch is cancelled
        mouseLeftPressed = false;
        mouseRightPressed = false;
        return true;
    }
    
    /**
     * Dispose Resources
     * 
     * Cleans up and releases all resources used by this manager.
     * Must be called when the game is disposed to prevent memory leaks.
     */
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}