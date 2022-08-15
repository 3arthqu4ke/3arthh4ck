package me.earth.earthhack.pingbypass.input;

import me.earth.earthhack.pingbypass.PingBypass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO
public class Mouse {
    private static final Map<Integer, Boolean> STATES = new ConcurrentHashMap<>();

    public static boolean isButtonDown(int button) {
        if (PingBypass.isConnected()) {
            return STATES.getOrDefault(button, false);
        }

        return org.lwjgl.input.Mouse.isButtonDown(button);
    }

    public static void setButtonDown(int button, boolean down) {
        STATES.put(button, down);
    }

    public static int getX() {
        return org.lwjgl.input.Mouse.getX();
    }

    public static int getY() {
        return org.lwjgl.input.Mouse.getY();
    }

    public static int getDWheel() {
        return org.lwjgl.input.Mouse.getDWheel();
    }

    public static void setGrabbed(boolean grabbed) {
        org.lwjgl.input.Mouse.setGrabbed(grabbed);
    }

    public static void clear() {
        STATES.clear();
    }

}
