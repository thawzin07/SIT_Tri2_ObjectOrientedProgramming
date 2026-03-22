package com.sit.inf1009.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public final class LeaderboardNameEditor {

    public static final class Result {
        private final String updatedName;
        private final boolean confirmed;
        private final boolean canceled;

        private Result(String updatedName, boolean confirmed, boolean canceled) {
            this.updatedName = updatedName;
            this.confirmed = confirmed;
            this.canceled = canceled;
        }

        public String getUpdatedName() {
            return updatedName;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public boolean isCanceled() {
            return canceled;
        }
    }

    private LeaderboardNameEditor() {
    }

    public static Result update(String currentName, int maxLength) {
        String name = currentName == null ? "" : currentName;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return new Result(name, true, false);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return new Result(name, false, true);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (!name.isEmpty()) {
                name = name.substring(0, name.length() - 1);
            }
            return new Result(name, false, false);
        }

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        name = appendIfPressed(name, maxLength, Input.Keys.SPACE, ' ');
        name = appendIfPressed(name, maxLength, Input.Keys.MINUS, shift ? '_' : '-');
        name = appendIfPressed(name, maxLength, Input.Keys.PERIOD, '.');

        name = appendIfPressed(name, maxLength, Input.Keys.NUM_0, '0');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_1, '1');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_2, '2');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_3, '3');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_4, '4');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_5, '5');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_6, '6');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_7, '7');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_8, '8');
        name = appendIfPressed(name, maxLength, Input.Keys.NUM_9, '9');

        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_0, '0');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_1, '1');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_2, '2');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_3, '3');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_4, '4');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_5, '5');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_6, '6');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_7, '7');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_8, '8');
        name = appendIfPressed(name, maxLength, Input.Keys.NUMPAD_9, '9');

        name = appendIfPressed(name, maxLength, Input.Keys.A, shift ? 'A' : 'a');
        name = appendIfPressed(name, maxLength, Input.Keys.B, shift ? 'B' : 'b');
        name = appendIfPressed(name, maxLength, Input.Keys.C, shift ? 'C' : 'c');
        name = appendIfPressed(name, maxLength, Input.Keys.D, shift ? 'D' : 'd');
        name = appendIfPressed(name, maxLength, Input.Keys.E, shift ? 'E' : 'e');
        name = appendIfPressed(name, maxLength, Input.Keys.F, shift ? 'F' : 'f');
        name = appendIfPressed(name, maxLength, Input.Keys.G, shift ? 'G' : 'g');
        name = appendIfPressed(name, maxLength, Input.Keys.H, shift ? 'H' : 'h');
        name = appendIfPressed(name, maxLength, Input.Keys.I, shift ? 'I' : 'i');
        name = appendIfPressed(name, maxLength, Input.Keys.J, shift ? 'J' : 'j');
        name = appendIfPressed(name, maxLength, Input.Keys.K, shift ? 'K' : 'k');
        name = appendIfPressed(name, maxLength, Input.Keys.L, shift ? 'L' : 'l');
        name = appendIfPressed(name, maxLength, Input.Keys.M, shift ? 'M' : 'm');
        name = appendIfPressed(name, maxLength, Input.Keys.N, shift ? 'N' : 'n');
        name = appendIfPressed(name, maxLength, Input.Keys.O, shift ? 'O' : 'o');
        name = appendIfPressed(name, maxLength, Input.Keys.P, shift ? 'P' : 'p');
        name = appendIfPressed(name, maxLength, Input.Keys.Q, shift ? 'Q' : 'q');
        name = appendIfPressed(name, maxLength, Input.Keys.R, shift ? 'R' : 'r');
        name = appendIfPressed(name, maxLength, Input.Keys.S, shift ? 'S' : 's');
        name = appendIfPressed(name, maxLength, Input.Keys.T, shift ? 'T' : 't');
        name = appendIfPressed(name, maxLength, Input.Keys.U, shift ? 'U' : 'u');
        name = appendIfPressed(name, maxLength, Input.Keys.V, shift ? 'V' : 'v');
        name = appendIfPressed(name, maxLength, Input.Keys.W, shift ? 'W' : 'w');
        name = appendIfPressed(name, maxLength, Input.Keys.X, shift ? 'X' : 'x');
        name = appendIfPressed(name, maxLength, Input.Keys.Y, shift ? 'Y' : 'y');
        name = appendIfPressed(name, maxLength, Input.Keys.Z, shift ? 'Z' : 'z');

        return new Result(name, false, false);
    }

    private static String appendIfPressed(String current, int maxLength, int keycode, char value) {
        if (!Gdx.input.isKeyJustPressed(keycode)) {
            return current;
        }
        if (current.length() >= maxLength) {
            return current;
        }
        return current + value;
    }
}
