package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import java.awt.event.*;
import javax.swing.JComponent;

/**
 * Captures raw mouse events and dispatches the appropriate IOEvent type.
 * Raw MouseEvent / MouseWheelEvent is the payload.
 */
public class MouseInputHandler extends AbstractInputHandler {

    private final JComponent target;
    private final MouseAdapter mouseAdapter;
    private final MouseMotionAdapter motionAdapter;
    private final MouseWheelListener wheelListener;

    public MouseInputHandler(InputOutputManager ioManager, JComponent target) {
        super(ioManager);
        this.target = target;

        this.mouseAdapter = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { dispatch(new IOEvent(IOEvent.Type.MOUSE_CLICKED,  e)); }
            @Override public void mousePressed(MouseEvent e)  { dispatch(new IOEvent(IOEvent.Type.MOUSE_PRESSED,  e)); }
            @Override public void mouseReleased(MouseEvent e) { dispatch(new IOEvent(IOEvent.Type.MOUSE_RELEASED, e)); }
        };

        this.motionAdapter = new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e)   { dispatch(new IOEvent(IOEvent.Type.MOUSE_MOVED,   e)); }
            @Override public void mouseDragged(MouseEvent e) { dispatch(new IOEvent(IOEvent.Type.MOUSE_DRAGGED, e)); }
        };

        this.wheelListener = e -> dispatch(new IOEvent(IOEvent.Type.MOUSE_WHEEL, e));

        target.addMouseListener(this.mouseAdapter);
        target.addMouseMotionListener(this.motionAdapter);
        target.addMouseWheelListener(this.wheelListener);
    }

    @Override
    public void detach() {
        target.removeMouseListener(mouseAdapter);
        target.removeMouseMotionListener(motionAdapter);
        target.removeMouseWheelListener(wheelListener);
    }
}