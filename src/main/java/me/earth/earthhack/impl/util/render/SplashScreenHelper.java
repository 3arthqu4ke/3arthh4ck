package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.forge.util.ForgeSplash;
import me.earth.earthhack.vanilla.Environment;

public class SplashScreenHelper {
    public static void setSplashScreen(String message, int steps) {
        if (Environment.hasForge()) {
            ForgeSplash.push(message, steps);
        }
    }

    public static void setSubStep(String message) {
        if (Environment.hasForge()) {
            ForgeSplash.setSubStep(message);
        }
    }

    public static void clear() {
        if (Environment.hasForge()) {
            ForgeSplash.clear();
        }
    }

}
