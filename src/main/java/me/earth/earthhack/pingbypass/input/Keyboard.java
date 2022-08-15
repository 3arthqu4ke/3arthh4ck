package me.earth.earthhack.pingbypass.input;

import me.earth.earthhack.pingbypass.PingBypass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO
public class Keyboard {
    private static final Map<Integer, Boolean> STATES = new ConcurrentHashMap<>();

    public static int getKeyboardSize() {
        return org.lwjgl.input.Keyboard.KEYBOARD_SIZE;
    }

    public static String getKeyName(int key) {
        return org.lwjgl.input.Keyboard.getKeyName(key);
    }

    public static int getRControl() {
        return org.lwjgl.input.Keyboard.KEY_RCONTROL;
    }

    public static int getLControl() {
        return org.lwjgl.input.Keyboard.KEY_LCONTROL;
    }

    public static int getLMenu() {
        return org.lwjgl.input.Keyboard.KEY_LMENU;
    }

    public static int getEscape() {
        return org.lwjgl.input.Keyboard.KEY_ESCAPE;
    }

    public static int getSpace() {
        return org.lwjgl.input.Keyboard.KEY_SPACE;
    }

    public static int getDelete() {
        return org.lwjgl.input.Keyboard.KEY_DELETE;
    }

    public static int getNone() {
        return org.lwjgl.input.Keyboard.KEY_NONE;
    }

    public static int getKeyV() {
        return org.lwjgl.input.Keyboard.KEY_V;
    }

    public static void enableRepeatEvents(boolean enable) {
        org.lwjgl.input.Keyboard.enableRepeatEvents(enable);
    }

    public static boolean getEventKeyState() {
        return org.lwjgl.input.Keyboard.getEventKeyState();
    }

    public static int getEventKey() {
        return org.lwjgl.input.Keyboard.getEventKey();
    }

    public static char getEventCharacter() {
        return org.lwjgl.input.Keyboard.getEventCharacter();
    }

    public static int getKeyIndex(String string) {
        return org.lwjgl.input.Keyboard.getKeyIndex(string);
    }

    public static int getKeyM() {
        return org.lwjgl.input.Keyboard.KEY_M;
    }

    public static boolean isKeyDown(int code) {
        if (PingBypass.isConnected()) {
            return STATES.getOrDefault(code, false);
        }

        return org.lwjgl.input.Keyboard.isKeyDown(code);
    }

}
