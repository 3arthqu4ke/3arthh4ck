package me.earth.earthhack.impl.core;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.core.transfomer.EarthhackTransformer;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.tweaker.EarthhackTweaker;
import me.earth.earthhack.tweaker.TweakerCore;
import me.earth.earthhack.vanilla.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 3arthh4cks CoreMod.
 * {@link EarthhackTweaker}
 */
@SuppressWarnings("unused")
public class Core implements TweakerCore
{
    /** Logger for the Core. */
    public static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");

    /**
     * Creates 3arthh4cks Files, starts Mixin, uses the
     * {@link PluginManager} to get PluginConfigs and
     * registers their, the given and the
     * mixins.earth.json mixinConfigs in Mixin.
     *
     * @param pluginClassLoader the classLoader to load the Plugins with.
     */
    @Override
    public void init(ClassLoader pluginClassLoader)
    {
        LOGGER.info("Initializing 3arthh4cks Core.");
        LOGGER.info("Found Environment: " + Environment.getEnvironment());
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());

        Path path = Paths.get("earthhack");
        FileUtil.createDirectory(path);
        FileUtil.getDirectory(path, "util");
        FileUtil.getDirectory(path, "plugins");

        PluginManager.getInstance().createPluginConfigs(pluginClassLoader);

        MixinBootstrap.init();
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                        .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT)
                        .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT)
                        .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                        .setSide(MixinEnvironment.Side.CLIENT);
        String extraMixin;
        if (Environment.hasForge())
        {
            LOGGER.info("Forge detected!");
            extraMixin = "mixins.forge.json";
        }
        else
        {
            LOGGER.info("No Forge!");
            extraMixin = "mixins.vanilla.json";
        }

        Mixins.addConfiguration(extraMixin);

        for (PluginConfig config : PluginManager.getInstance()
                                                .getConfigs()
                                                .values())
        {
            if (config.getMixinConfig() != null)
            {
                LOGGER.info("Adding "
                             + config.getName()
                             + "'s MixinConfig: "
                             + config.getMixinConfig());

                Mixins.addConfiguration(config.getMixinConfig());
            }
        }

        Mixins.addConfiguration("mixins.earth.json");
        String obfuscationContext = "searge";
        if (Environment.getEnvironment() == Environment.VANILLA)
        {
            obfuscationContext = "notch";
        }

        MixinEnvironment.getDefaultEnvironment()
                        .setObfuscationContext(obfuscationContext);
    }

    @Override
    public String[] getTransformers()
    {
        return new String[] { EarthhackTransformer.class.getName() };
    }

}
