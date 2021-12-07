package me.earth.earthhack.installer.main;

import java.io.File;

public class MinecraftFiles
{
    private String minecraft;
    private String libraries;
    private String versions;

    public String getMinecraft()
    {
        return minecraft;
    }

    public String getLibraries()
    {
        return libraries;
    }

    public String getVersions()
    {
        return versions;
    }

    public void findFiles(String[] args)
    {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nux"))
        {
            minecraft = System.getProperty("user.home") + "/.minecraft/";
        }
        else if (os.contains("darwin") || os.contains("mac"))
        {
            minecraft = System.getProperty("user.home")
                    + "/Library/Application Support/minecraft/";
        }
        else if (os.contains("win"))
        {
            minecraft = System.getenv("APPDATA")
                    + File.separator + ".minecraft" + File.separator;
        }

        if (minecraft != null)
        {
            libraries = minecraft + "libraries" + File.separator;
            versions  = minecraft + "versions"  + File.separator;
            return;
        }

        if (args.length < 3)
        {
            throw new IllegalStateException("Unknown OS," +
                    " please specify minecraft," +
                    " libraries and versions folders as main args!");
        }

        minecraft = args[0];
        libraries = args[1];
        versions  = args[2];
    }

}
