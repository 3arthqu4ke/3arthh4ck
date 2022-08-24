package me.earth.earthhack.forge.util;

import me.earth.earthhack.tweaker.launch.DevArguments;
import net.minecraftforge.fml.common.ProgressManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Wraps Forges ProgressManager since it's not available on Vanilla.
 */
public class ForgeSplashHelper {
    private static ProgressManager.ProgressBar bar;

    public static void push(String message, int steps) {
        if (!(boolean) DevArguments.getInstance()
                                   .getArgument("splash")
                                   .getValue()) {
            return;
        }

        try {
            Field field = ProgressManager.class.getDeclaredField("bars");
            field.setAccessible(true);
            List<?> list = (List<?>) field.get(null);
            list.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        bar = ProgressManager.push(message, steps, true);
    }

    public static void setSubStep(String step) {
        ProgressManager.ProgressBar bar = ForgeSplashHelper.bar;
        if (bar != null) {
            bar.step(step);
        }
    }

    public static void clear() {
        ProgressManager.ProgressBar bar = ForgeSplashHelper.bar;
        if (bar != null) {
            ProgressManager.pop(bar);
        }

        ForgeSplashHelper.bar = null;
    }

}
