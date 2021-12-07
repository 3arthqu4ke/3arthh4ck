package me.earth.earthhack.installer;

import java.util.concurrent.atomic.AtomicBoolean;

public class InstallerGlobals
{
    private static final AtomicBoolean VANILLA  = new AtomicBoolean();
    private static final AtomicBoolean FORGE    = new AtomicBoolean();

    public static void setVanilla(boolean vanilla)
    {
        VANILLA.set(vanilla);
    }

    public static void setForge(boolean forge)
    {
        FORGE.set(forge);
    }

    public static boolean hasInstalledVanilla()
    {
        return VANILLA.get();
    }

    public static boolean hasInstalledForge()
    {
        return FORGE.get();
    }

}
