package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.forge.util.ForgeSplashHelper;
import me.earth.earthhack.vanilla.Environment;

public class SplashScreenHelper {
    public static void setSplashScreen(String message, int steps) {
        if (Environment.hasForge()) {
            ForgeSplashHelper.push(message, steps);
        }
    }

    public static void setSubStep(String message) {
        if (Environment.hasForge()) {
            ForgeSplashHelper.setSubStep(message);
        }
    }

    public static void clear() {
        if (Environment.hasForge()) {
            ForgeSplashHelper.clear();
        }
    }

}
