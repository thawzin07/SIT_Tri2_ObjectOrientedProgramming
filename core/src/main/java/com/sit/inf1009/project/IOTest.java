package com.sit.inf1009.project;

import com.sit.inf1009.project.engine.core.handlers.*;
import com.sit.inf1009.project.engine.interfaces.OutputHandler;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Standalone IO isolation test — both input AND output sides.
 *
 * No real managers needed. Anonymous classes and lambdas fake their behaviour.
 * Run this class directly and interact with the window.
 *
 * Input tests:
 *  W A S D   → FakeMovementManager reacts
 *  SPACE     → FakeSimulationManager toggles pause
 *  ENTER     → FakeEntityManager spawns bullet
 *  Click     → MOUSE_CLICKED fires
 *  Resize    → WINDOW_RESIZED fires

 *
 * Output tests:
 *  H  → SOUND_PLAY "hit" + DISPLAY_EFFECT "flash"
 *  B  → SOUND_PLAY "shoot"
 *  F  → DISPLAY_EFFECT "flash"
 *  M  → DISPLAY_SHOW_HUD "Game Paused"
 *  X  → SOUND_STOP_ALL
 */
public class IOTest {

    private static final String SEP = "-------------------------------------------------------";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IOTest::run);
    }

    private static void run() {

        // Swing setup
        JFrame frame = new JFrame("IO Test - Input & Output");
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setPreferredSize(new Dimension(640, 400));

        JLabel hint = new JLabel(
            "<html><center><font color='white' size='4'>" +
            "<b>Input:</b> W A S D | Space (pause) | Enter (shoot) | Click | Resize<br>" +
            "<b>Output:</b> H=hit sound | B=shoot sound | F=flash | M=HUD | X=stop sounds<br>" +
            "D = disable IO for 3s" +
            "</font></center></html>",
            SwingConstants.CENTER
        );
        hint.setOpaque(false);
        panel.add(hint, BorderLayout.CENTER);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        // IO setup
        InputOutputManager io = new InputOutputManager();
        io.enableLogger(true);

        // Input handlers
        io.registerInputHandler(new KeyboardInputHandler(io, panel));
        io.registerInputHandler(new MouseInputHandler(io, panel));
        io.registerInputHandler(new WindowInputHandler(io, frame));

        // Real sound output handler
        io.registerOutputHandler(new SoundOutputHandler());

        // Fake display output handler
        // Must be anonymous class — OutputHandler has two methods (onIOEvent + close)
        // so it cannot be used as a lambda
        io.registerOutputHandler(new OutputHandler() {
            @Override
            public void onIOEvent(IOEvent event) {
                if (event.getType() == IOEvent.Type.DISPLAY_EFFECT) {
                    System.out.println("[FakeDisplayOutput] Visual effect: "
                        + event.getPayload(String.class));
                } else if (event.getType() == IOEvent.Type.DISPLAY_SHOW_HUD) {
                    System.out.println("[FakeDisplayOutput] HUD message: "
                        + event.getPayload(String.class));
                } else if (event.getType() == IOEvent.Type.DISPLAY_RENDER) {
                    System.out.println("[FakeDisplayOutput] Render frame requested");
                }
            }

            @Override
            public void close() {
                System.out.println("[FakeDisplayOutput] Closed");
            }
        });

        printHeader("IO ONLINE - interact with the window");

        // Fake MovementManager — WASD
        io.addListener(IOEvent.Type.KEY_PRESSED, event -> {
            KeyEvent key = event.getPayload(KeyEvent.class);
            int code = key.getKeyCode();

            if (code == KeyEvent.VK_W) {
                System.out.println("[FakeMovementManager] Move UP");
            } else if (code == KeyEvent.VK_A) {
                System.out.println("[FakeMovementManager] Move LEFT");
            } else if (code == KeyEvent.VK_S) {
                System.out.println("[FakeMovementManager] Move DOWN");
            } else if (code == KeyEvent.VK_D) {
                System.out.println("[FakeMovementManager] Move RIGHT");
            }
        });

        // Fake SimulationManager — SPACE
        final boolean[] paused = {false};
        io.addListener(IOEvent.Type.KEY_PRESSED, event -> {
            KeyEvent key = event.getPayload(KeyEvent.class);
            if (key.getKeyCode() == KeyEvent.VK_SPACE) {
                paused[0] = !paused[0];
                String state = paused[0] ? "PAUSED" : "RESUMED";
                System.out.println("[FakeSimulationManager] Simulation " + state);
                io.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD,
                    paused[0] ? "Game Paused" : "Game Resumed"));
            }
        });

        // Fake EntityManager — ENTER shoots
        io.addListener(IOEvent.Type.KEY_PRESSED, event -> {
            KeyEvent key = event.getPayload(KeyEvent.class);
            if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                System.out.println("[FakeEntityManager] Bullet spawned!");
                io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "shoot"));
            }
        });

        // Output trigger keys — simulate managers calling sendOutput
        io.addListener(IOEvent.Type.KEY_PRESSED, event -> {
            KeyEvent key = event.getPayload(KeyEvent.class);
            int code = key.getKeyCode();

            if (code == KeyEvent.VK_H) {
                System.out.println("[FakeCollisionManager] Hit! Sending output...");
                io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "hit"));
                io.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
            } else if (code == KeyEvent.VK_B) {
                System.out.println("[FakeEntityManager] Shoot sound via output...");
                io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "shoot"));
            } else if (code == KeyEvent.VK_F) {
                System.out.println("[FakeCollisionManager] Flash effect via output...");
                io.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
            } else if (code == KeyEvent.VK_M) {
                System.out.println("[FakeSimulationManager] Sending HUD message...");
                io.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "Game Paused"));
            } else if (code == KeyEvent.VK_X) {
                System.out.println("[FakeSimulationManager] Stopping all sounds...");
                io.sendOutput(new IOEvent(IOEvent.Type.SOUND_STOP_ALL, null));
            }
        });

        // Fake SceneManager — window events
        io.addListener(IOEvent.Type.WINDOW_RESIZED, event -> {
            Dimension size = event.getPayload(Dimension.class);
            System.out.printf("[FakeSceneManager] Viewport updated: %dx%d%n",
                size.width, size.height);
        });

        io.addListener(IOEvent.Type.WINDOW_FOCUS_LOST, event ->
            System.out.println("[FakeSceneManager] Focus lost - render loop would pause"));

        io.addListener(IOEvent.Type.WINDOW_FOCUS_GAINED, event ->
            System.out.println("[FakeSceneManager] Focus gained - render loop would resume"));

        // Fake EntityManager — mouse clicks
        io.addListener(IOEvent.Type.MOUSE_CLICKED, event -> {
            MouseEvent mouse = event.getPayload(MouseEvent.class);
            System.out.printf("[FakeEntityManager] Click at (%d, %d)%n",
                mouse.getX(), mouse.getY());
        });

        // App — window close
        io.addListener(IOEvent.Type.WINDOW_CLOSED, event -> {
            printHeader("WINDOW CLOSED - shutting down");
            io.shutdown();
            System.out.println("[App] io.shutdown() complete");
            System.exit(0);
        });
    }

    private static void printHeader(String msg) {
        System.out.println(SEP);
        System.out.println("  " + msg);
        System.out.println(SEP);
    }
}