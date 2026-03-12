package com.sit.inf1009.project.engine.managers;

/**
 * libGDX-friendly mouse event payload used by IOEvent mouse types.
 */
public record MouseEventData(
        int x,
        int y,
        int button,
        int pointer,
        float scrollAmountX,
        float scrollAmountY
) {
    public static MouseEventData pointer(int x, int y, int button, int pointer) {
        return new MouseEventData(x, y, button, pointer, 0f, 0f);
    }

    public static MouseEventData move(int x, int y) {
        return new MouseEventData(x, y, -1, -1, 0f, 0f);
    }

    public static MouseEventData wheel(float amountX, float amountY) {
        return new MouseEventData(-1, -1, -1, -1, amountX, amountY);
    }
}
