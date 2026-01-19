//package com.sit.inf1009.project;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.math.Vector2;
//import com.sit.inf1009.project.engine.managers.InputOutputManager;
//
///**
// * Main Test Class for InputOutputManager
// * 
// * This class tests all functionality of the InputOutputManager:
// * - Keyboard input (held and just pressed)
// * - Mouse input (buttons and position)
// * - Text rendering
// * - Debug output
// * 
// * @author INF1009 Team Project
// * @version 1.0
// */
//public class Main extends ApplicationAdapter {
//    
//    // Test tracking variables
//    private int frameCount;
//    private boolean testsPassed;
//    private StringBuilder testResults;
//    
//    /**
//     * Create Method
//     * 
//     * Called once when the application starts.
//     * Initializes the InputOutputManager and test tracking.
//     */
//    @Override
//    public void create() {
//        // Initialize InputOutputManager
//        InputOutputManager.getInstance().initialize();
//        
//        frameCount = 0;
//        testsPassed = true;
//        testResults = new StringBuilder();
//        
//        System.out.println("===========================================");
//        System.out.println("InputOutputManager Testing Started");
//        System.out.println("===========================================");
//        System.out.println("TEST 1: Singleton Pattern");
//        testSingletonPattern();
//        
//        System.out.println("\nINSTRUCTIONS FOR MANUAL TESTING:");
//        System.out.println("- Press SPACE to test key held detection");
//        System.out.println("- Press ENTER to test key just pressed detection");
//        System.out.println("- Press W, A, S, D to test arrow keys");
//        System.out.println("- Click LEFT MOUSE to test left click");
//        System.out.println("- Click RIGHT MOUSE to test right click");
//        System.out.println("- Move MOUSE to test position tracking");
//        System.out.println("- Press ESC to exit");
//        System.out.println("===========================================\n");
//    }
//    
//    /**
//     * Render Method
//     * 
//     * Called every frame (60 times per second).
//     * Tests all input/output functionality continuously.
//     */
//    @Override
//    public void render() {
//        // Clear screen with dark gray background
//        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        
//        // Update InputOutputManager (CRITICAL - must be called every frame)
//        InputOutputManager.getInstance().update();
//        
//        frameCount++;
//        
//        // Run tests
//        testKeyboardHeld();
//        testKeyboardJustPressed();
//        testMouseButtons();
//        testMousePosition();
//        testTextRendering();
//        
//        // Display test results on screen
//        displayTestResults();
//        
//        // Exit on ESC key
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.ESCAPE)) {
//            Gdx.app.exit();
//        }
//    }
//    
//    /**
//     * Test Singleton Pattern
//     * 
//     * Verifies that getInstance() always returns the same instance.
//     */
//    private void testSingletonPattern() {
//        InputOutputManager instance1 = InputOutputManager.getInstance();
//        InputOutputManager instance2 = InputOutputManager.getInstance();
//        
//        if (instance1 == instance2) {
//            System.out.println("✅ PASS: Singleton pattern working correctly");
//        } else {
//            System.out.println("❌ FAIL: Multiple instances created!");
//            testsPassed = false;
//        }
//    }
//    
//    /**
//     * Test Keyboard Held Detection
//     * 
//     * Tests if isKeyPressed() correctly detects held keys.
//     */
//    private void testKeyboardHeld() {
//        // Test SPACE key
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.SPACE)) {
//            System.out.println("✅ SPACE key is being HELD (Frame: " + frameCount + ")");
//        }
//        
//        // Test WASD keys
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.W)) {
//            System.out.println("✅ W key detected");
//        }
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.A)) {
//            System.out.println("✅ A key detected");
//        }
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.S)) {
//            System.out.println("✅ S key detected");
//        }
//        if (InputOutputManager.getInstance().isKeyPressed(Input.Keys.D)) {
//            System.out.println("✅ D key detected");
//        }
//    }
//    
//    /**
//     * Test Keyboard Just Pressed Detection
//     * 
//     * Tests if isKeyJustPressed() correctly detects single key presses.
//     */
//    private void testKeyboardJustPressed() {
//        // Test ENTER key (should only print once per press)
//        if (InputOutputManager.getInstance().isKeyJustPressed(Input.Keys.ENTER)) {
//            System.out.println("✅ ENTER key JUST PRESSED (should only appear once per press)");
//        }
//        
//        // Test SPACE with just pressed (compare with held)
//        if (InputOutputManager.getInstance().isKeyJustPressed(Input.Keys.SPACE)) {
//            System.out.println("✅ SPACE key JUST PRESSED (first frame only)");
//        }
//    }
//    
//    /**
//     * Test Mouse Button Detection
//     * 
//     * Tests mouse button press detection for left and right buttons.
//     */
//    private void testMouseButtons() {
//        // Test left mouse button (held)
//        if (InputOutputManager.getInstance().isMouseLeftPressed()) {
//            Vector2 pos = InputOutputManager.getInstance().getMousePosition();
//            System.out.println("✅ LEFT MOUSE held at position: (" + 
//                (int)pos.x + ", " + (int)pos.y + ")");
//        }
//        
//        // Test left mouse button (just pressed)
//        if (InputOutputManager.getInstance().isMouseLeftJustPressed()) {
//            System.out.println("✅ LEFT MOUSE JUST CLICKED");
//        }
//        
//        // Test right mouse button (held)
//        if (InputOutputManager.getInstance().isMouseRightPressed()) {
//            Vector2 pos = InputOutputManager.getInstance().getMousePosition();
//            System.out.println("✅ RIGHT MOUSE held at position: (" + 
//                (int)pos.x + ", " + (int)pos.y + ")");
//        }
//        
//        // Test right mouse button (just pressed)
//        if (InputOutputManager.getInstance().isMouseRightJustPressed()) {
//            System.out.println("✅ RIGHT MOUSE JUST CLICKED");
//        }
//    }
//    
//    /**
//     * Test Mouse Position Tracking
//     * 
//     * Tests if mouse position is tracked correctly.
//     * Prints position every 60 frames to avoid spam.
//     */
//    private void testMousePosition() {
//        // Only print every 60 frames to avoid console spam
//        if (frameCount % 60 == 0) {
//            Vector2 mousePos = InputOutputManager.getInstance().getMousePosition();
//            System.out.println("Mouse Position: (" + (int)mousePos.x + ", " + (int)mousePos.y + ")");
//        }
//    }
//    
//    /**
//     * Test Text Rendering
//     * 
//     * Tests if text rendering works correctly.
//     */
//    private void testTextRendering() {
//        // Test basic text rendering
//        InputOutputManager.getInstance().renderText(
//            "InputOutputManager Test Suite", 
//            50.0, 
//            (double)Gdx.graphics.getHeight() - 30.0
//        );
//        
//        // Test debug info rendering
//        InputOutputManager.getInstance().renderDebugInfo(
//            "FPS: " + Gdx.graphics.getFramesPerSecond() + " | Frame: " + frameCount
//        );
//    }
//    
//    /**
//     * Display Test Results
//     * 
//     * Renders comprehensive test results on screen.
//     */
//    private void displayTestResults() {
//        double startY = (double)Gdx.graphics.getHeight() - 80.0;
//        double lineHeight = 20.0;
//        double currentY = startY;
//        
//        // Title
//        InputOutputManager.getInstance().renderText(
//            "=== KEYBOARD TESTS ===", 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight;
//        
//        // Keyboard status
//        String spaceStatus = InputOutputManager.getInstance().isKeyPressed(Input.Keys.SPACE) 
//            ? "PRESSED" : "not pressed";
//        InputOutputManager.getInstance().renderText(
//            "SPACE: " + spaceStatus, 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight;
//        
//        String enterStatus = InputOutputManager.getInstance().isKeyPressed(Input.Keys.ENTER) 
//            ? "PRESSED" : "not pressed";
//        InputOutputManager.getInstance().renderText(
//            "ENTER: " + enterStatus, 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight * 1.5;
//        
//        // Mouse tests
//        InputOutputManager.getInstance().renderText(
//            "=== MOUSE TESTS ===", 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight;
//        
//        Vector2 mousePos = InputOutputManager.getInstance().getMousePosition();
//        InputOutputManager.getInstance().renderText(
//            "Position: (" + (int)mousePos.x + ", " + (int)mousePos.y + ")", 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight;
//        
//        String leftStatus = InputOutputManager.getInstance().isMouseLeftPressed() 
//            ? "PRESSED" : "not pressed";
//        InputOutputManager.getInstance().renderText(
//            "Left Button: " + leftStatus, 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight;
//        
//        String rightStatus = InputOutputManager.getInstance().isMouseRightPressed() 
//            ? "PRESSED" : "not pressed";
//        InputOutputManager.getInstance().renderText(
//            "Right Button: " + rightStatus, 
//            50.0, 
//            currentY
//        );
//        currentY -= lineHeight * 1.5;
//        
//        // Instructions
//        InputOutputManager.getInstance().renderText(
//            "Press ESC to exit", 
//            50.0, 
//            currentY
//        );
//    }
//    
//    /**
//     * Dispose Method
//     * 
//     * Called when application closes.
//     * Cleans up resources and prints final test results.
//     */
//    @Override
//    public void dispose() {
//        InputOutputManager.getInstance().dispose();
//        
//        System.out.println("\n===========================================");
//        System.out.println("InputOutputManager Testing Complete");
//        System.out.println("===========================================");
//        System.out.println("Total frames rendered: " + frameCount);
//        
//        if (testsPassed) {
//            System.out.println("✅ ALL AUTOMATED TESTS PASSED");
//        } else {
//            System.out.println("❌ SOME TESTS FAILED - Check logs above");
//        }
//        
//        System.out.println("===========================================");
//    }
//}
//
//public class MainInputOuputManagerTester {
//
//}
