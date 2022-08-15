package me.earth.earthhack.tweaker;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.tweaker.launch.ArgumentManager;
import me.earth.earthhack.tweaker.launch.DevArguments;
import me.earth.earthhack.tweaker.launch.arguments.BooleanArgument;
import me.earth.earthhack.tweaker.launch.arguments.LongArgument;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinTweaker;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads {@link Core}.
 *
 * --tweakClass me.earth.earthhack.tweaker.EarthhackTweaker
 */
public class EarthhackTweaker implements ITweaker
{
    private Map<String, String> launchArgs;
    private final MixinTweaker wrapped;

    public EarthhackTweaker()
    {
        wrapped = new MixinTweaker();
    }

    @Override
    public void acceptOptions(List<String> args,
                              File gameDir,
                              File assetsDir,
                              String profile)
    {
        try
        {
            String className = "me.earth.earthhack.vanilla.Environment";
            Class<?> env = Class.forName(className, true, Launch.classLoader);
            Method load = env.getDeclaredMethod("loadEnvironment");
            load.setAccessible(true);
            load.invoke(null);
        }
        catch (ClassNotFoundException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }

        Object obj = Launch.blackboard.get("launchArgs");
        if (obj == null)
        {
            this.launchArgs = new HashMap<>();
            String classifier = null;
            for (String arg : args)
            {
                if (arg.startsWith("-"))
                {
                    if (classifier != null)
                    {
                        classifier = launchArgs.put(classifier, "");
                    }
                    else if (arg.contains("="))
                    {
                        classifier = launchArgs.put(
                                arg.substring(0, arg.indexOf('=')),
                                arg.substring(arg.indexOf('=') + 1));
                    }
                    else
                    {
                        classifier = arg;
                    }
                }
                else
                {
                    if (classifier != null)
                    {
                        classifier = launchArgs.put(classifier, arg);
                    }
                }
            }

            if (!this.launchArgs.containsKey("--version"))
            {
                launchArgs.put("--version", profile != null
                        ? profile
                        : "3arthh4ck-Profile");
            }

            if (!this.launchArgs.containsKey("--gameDir")
                    && gameDir != null)
            {
                launchArgs.put("--gameDir", gameDir.getAbsolutePath());
            }

            if (!this.launchArgs.containsKey("--assetsDir")
                    && assetsDir != null)
            {
                launchArgs.put("--assetsDir", assetsDir.getAbsolutePath());
            }
        }

        wrapped.acceptOptions(args, gameDir, assetsDir, profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        wrapped.injectIntoClassLoader(classLoader);

        try
        {
            String className = "me.earth.earthhack.impl.core.Core";
            classLoader.addTransformerExclusion(className);
            Class<?> coreClass = Class.forName(className, true, classLoader);
            // this is kinda a mess idk...
            TweakerCore core = (TweakerCore) coreClass.newInstance();
            Logger logger = LogManager.getLogger("3arthh4ck-Core");
            logger.info("\n\n");

            loadDevArguments();
            core.init(classLoader);

            for (String transformer : core.getTransformers())
            {
                classLoader.registerTransformer(transformer);
            }

            logger.info("\n\n");
        }
        catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getLaunchTarget()
    {
        return wrapped.getLaunchTarget();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] getLaunchArguments()
    {
        List<String> al = (List<String>) Launch.blackboard.get("ArgumentList");
        if (al.isEmpty()
                && launchArgs != null
                && Launch.blackboard.get("launchArgs") == null)
        {
            List<String> args = new ArrayList<>(launchArgs.size() * 2);
            for (Map.Entry<String, String> arg : launchArgs.entrySet())
            {
                args.add(arg.getKey());
                args.add(arg.getValue());
            }

            return args.toArray(new String[0]);
        }

        return wrapped.getLaunchArguments();
    }

    private void loadDevArguments()
    {
        ArgumentManager dev = DevArguments.getInstance();
        dev.addArgument("inventory",   new BooleanArgument());
        dev.addArgument("inventorymp", new BooleanArgument());
        dev.addArgument("totems",      new BooleanArgument());
        dev.addArgument("dead",        new BooleanArgument(Boolean.FALSE));
        dev.addArgument("jsrn",        new BooleanArgument());
        dev.addArgument("jsnull",      new BooleanArgument(Boolean.FALSE));
        dev.addArgument("connection",  new LongArgument(800L));
        dev.addArgument("leijurvpos",  new BooleanArgument(Boolean.TRUE));
        dev.addArgument("splash",      new BooleanArgument(Boolean.TRUE));
        dev.loadArguments();
    }

}
