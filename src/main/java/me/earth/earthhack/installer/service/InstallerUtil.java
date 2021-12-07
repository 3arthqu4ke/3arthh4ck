package me.earth.earthhack.installer.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.installer.version.VersionUtil;

import java.util.Iterator;

public class InstallerUtil
{
    public static final String ASM    = "org.ow2.asm:asm-debug-all:5.2";
    public static final String LAUNCH = "net.minecraft:launchwrapper:1.12";
    public static final String NAME   = "earthhack:forge:1.12.2";
    public static final String VNAME  = "earthhack:vanilla:1.12.2";

    public static void installEarthhack(JsonObject o, boolean forge)
    {
        JsonElement args = VersionUtil.getOrElse(VersionUtil.ARGS, o, "");
        String newArgs = args.getAsString() + " " + VersionUtil.EARTH;
        JsonElement element = Jsonable.parse(newArgs);
        o.add(VersionUtil.ARGS, element);

        JsonElement libs = VersionUtil.getOrElse(VersionUtil.LIBS, o, "[]");
        JsonArray array = libs.getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("name", Jsonable.parse(forge ? NAME : VNAME));
        array.add(object);
        o.add(VersionUtil.LIBS, libs);
    }

    public static void installLibs(JsonObject o)
    {
        JsonElement libs = VersionUtil.getOrElse(VersionUtil.LIBS, o, "[]");
        JsonArray array = libs.getAsJsonArray();

        boolean hasAsm    = false;
        boolean hasLaunch = false;
        for (JsonElement element : array)
        {
            JsonElement name = element.getAsJsonObject().get("name");
            if (name != null)
            {
                if (name.getAsString().equals(ASM))
                {
                    hasAsm = true;
                }
                else if (name.getAsString().equals(LAUNCH))
                {
                    hasLaunch = true;
                }
            }
        }

        if (!hasAsm)
        {
            JsonObject asmLib = new JsonObject();
            asmLib.add("name", Jsonable.parse(ASM));

            JsonObject downloads = new JsonObject();

            JsonObject artifact = new JsonObject();
            artifact.add("path", Jsonable.parse(
                "org/ow2/asm/asm-debug-all/5.2/asm-debug-all-5.2.jar"));
            artifact.add("url", Jsonable.parse(
                "https://files.minecraftforge.net/maven/org/ow2/asm/" +
                        "asm-debug-all/5.2/asm-debug-all-5.2.jar"));
            artifact.add("sha1", Jsonable.parse(
                "3354e11e2b34215f06dab629ab88e06aca477c19"));
            artifact.add("size", Jsonable.parse(
                "387903", false));

            downloads.add("artifact", artifact);

            asmLib.add("downloads", downloads);
            asmLib.add("earthhlib", Jsonable.parse("true", false));

            array.add(asmLib);
        }

        if (!hasLaunch)
        {
            JsonObject launchLib = new JsonObject();

            launchLib.add("name",
                    Jsonable.parse("net.minecraft:launchwrapper:1.12"));
            launchLib.add("serverreq",
                    Jsonable.parse("true", false));
            launchLib.add("earthhlib",
                    Jsonable.parse("true", false));

            array.add(launchLib);
        }
    }

    public static void uninstallEarthhack(JsonObject o)
    {
        JsonElement args = o.get(VersionUtil.ARGS);
        if (args != null)
        {
            String removed = args.getAsString()
                    .replace(" " + VersionUtil.EARTH, "");

            JsonElement element = Jsonable.parse(removed);
            o.add(VersionUtil.ARGS, element);
        }

        JsonElement libs = o.get(VersionUtil.LIBS);
        if (libs != null)
        {
            JsonArray array = libs.getAsJsonArray();
            Iterator<JsonElement> itr = array.iterator();
            while (itr.hasNext())
            {
                JsonObject library = itr.next().getAsJsonObject();
                JsonElement name = library.get("name");
                if (name != null
                        && (name.getAsString().equals(NAME)
                        || name.getAsString().equals(VNAME)))
                {
                    itr.remove();
                }
            }
        }
    }

    public static void uninstallLibs(JsonObject o)
    {
        JsonElement libs = o.get(VersionUtil.LIBS);
        if (libs != null)
        {
            JsonArray array = libs.getAsJsonArray();
            Iterator<JsonElement> itr = array.iterator();
            while (itr.hasNext())
            {
                JsonObject library = itr.next().getAsJsonObject();
                JsonElement name = library.get("earthhlib");
                if (name != null)
                {
                    itr.remove();
                }
            }
        }
    }

}
