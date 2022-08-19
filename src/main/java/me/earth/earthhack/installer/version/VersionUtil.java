package me.earth.earthhack.installer.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;

public class VersionUtil
{
    public static final String MAIN = "net.minecraft.launchwrapper.Launch";
    public static final String EARTH = "--tweakClass me.earth.earthhack.tweaker.EarthhackTweaker";
    public static final String FORGE = "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker";
    public static final String ARGS = "minecraftArguments";
    public static final String LIBS = "libraries";
    
    public static boolean hasEarthhack(final Version version) {
        return containsArgument(version, "--tweakClass me.earth.earthhack.tweaker.EarthhackTweaker");
    }
    
    public static boolean hasForge(final Version version) {
        return containsArgument(version, "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker");
    }
    
    public static boolean hasLaunchWrapper(final Version version) {
        final JsonElement element = version.getJson().get("mainClass");
        return element != null && element.getAsString().equals("net.minecraft.launchwrapper.Launch");
    }
    
    public static boolean containsArgument(final Version version, final String tweaker) {
        final JsonElement element = version.getJson().get("minecraftArguments");
        return element != null && element.getAsString().contains(tweaker);
    }
    
    public static JsonElement getOrElse(final String name, final JsonObject object, final String returnElse) {
        final JsonElement element = object.get(name);
        if (element == null) {
            return Jsonable.PARSER.parse(returnElse);
        }
        return element;
    }
}
