package com.sit.inf1009.project.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class MenuInputHandler implements IOListener {

    private int activeBtn = -1;
    private final InputOutputManager ioManager;

    public MenuInputHandler(InputOutputManager ioManager) {
        this.ioManager = ioManager;
        ioManager.addGlobalListener(this);
    }

    public int getActiveBtn() {
        return activeBtn;
    }

    public void update(MenuButton[] btns, OrthographicCamera camera, Viewport viewport) {
        boolean touching = Gdx.input.isTouched();

        if (touching) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch,
                viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight());

            activeBtn = -1;
            for (int i = 0; i < btns.length; i++) {
                if (btns[i].bounds.contains(touch.x, touch.y)) {
                    activeBtn = i;
                    ioManager.handleEvent(
                        new IOEvent(IOEvent.Type.MOUSE_PRESSED, "btn:" + i)
                    );
                    break;
                }
            }
        }

        if (!touching) activeBtn = -1;
    }

    public boolean wasJustTouched() {
        return Gdx.input.justTouched();
    }

    public void detach() {
        ioManager.removeGlobalListener(this);
    }

    @Override
    public void onIOEvent(IOEvent event) {
        // Reserved for future keyboard navigation of menu
    }
}
