package com.sit.inf1009.project.engine.components;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class PlayerMovement extends MovementComponent implements IOListener {

    private boolean w, a, s, d;

    public PlayerMovement(InputOutputManager ioManager, double speed) {
        super(speed);
        ioManager.addListener(IOEvent.Type.KEY_PRESSED, this);
        ioManager.addListener(IOEvent.Type.KEY_RELEASED, this);
    }

    @Override
    public void onIOEvent(IOEvent event) {
        // only care about keyboard
        if (event.getType() != IOEvent.Type.KEY_PRESSED &&
            event.getType() != IOEvent.Type.KEY_RELEASED) return;

        boolean pressed = (event.getType() == IOEvent.Type.KEY_PRESSED);

        Integer keycode = event.getPayload(Integer.class);
        if (keycode == null) return;

        switch (keycode) {
        case Keys.W:
            w = pressed;
            break;
        case Keys.A:
            a = pressed;
            break;
        case Keys.S:
            s = pressed;
            break;
        case Keys.D:
            d = pressed;
            break;
        default:
            break;
    }

    }

    @Override
    public void update(Entity e, double dt) {
        double vx = 0, vy = 0;

        if (a) vx -= speed;
        if (d) vx += speed;
        if (w) vy += speed;
        if (s) vy -= speed;

        // normalize diagonal
        if (vx != 0 && vy != 0) {
            double inv = 1.0 / Math.sqrt(2);
            vx *= inv;
            vy *= inv;
        }

        double newX = e.getXPosition() + vx * dt;
        double newY = e.getYPosition() + vy * dt;

        e.setXPosition(newX);
        e.setYPosition(newY);
    }
}
