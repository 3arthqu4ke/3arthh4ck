package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.fullbright.Fullbright;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.render.WorldRenderUtil;
import me.earth.earthhack.vanilla.Environment;
import net.minecraft.block.Block;

import java.lang.reflect.Field;

public class XRay extends BlockAddingModule
{
    private static final ModuleCache<Fullbright> FULL_BRIGHT =
            Caches.getModule(Fullbright.class);

    protected final Setting<XrayMode> mode =
            register(new EnumSetting<>("Mode", XrayMode.Simple));
    protected final Setting<Boolean> soft =
            register(new BooleanSetting("Soft-Reload", false));
    protected final Setting<Integer> opacity =
            register(new NumberSetting<>("Opacity", 120, 0, 255));

    protected boolean lightPipeLine;

    public XRay()
    {
        super("XRay",
              Category.Render,
              s -> "Black/Whitelist " + s.getName() + " from being displayed.");
        this.listeners.add(new ListenerBlockLayer(this));
        this.listeners.add(new ListenerTick(this));
        mode.addObserver(event ->
        {
            if (this.isEnabled() && !event.isCancelled())
            {
                this.toggle();
                Scheduler.getInstance().schedule(this::toggle);
            }
        });
        this.listType.addObserver(event ->
        {
            if (this.isEnabled())
            {
                loadRenderers();
            }
        });

        this.setData(new XRayData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    public void onEnable()
    {
        if (mode.getValue() == XrayMode.Opacity)
        {
            if (Environment.hasForge())
            {
                try
                {
                    Field field = Class
                        .forName("net.minecraftforge.common.ForgeModContainer",
                                    true, this.getClass().getClassLoader())
                        .getDeclaredField("forgeLightPipelineEnabled");

                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    this.lightPipeLine = field.getBoolean(null);
                    field.set(null, false);
                    field.setAccessible(accessible);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (!FULL_BRIGHT.isEnabled())
            {
                mc.gameSettings.gammaSetting = 1.0F;
            }

            mc.renderChunksMany = false;
        }

        Scheduler.getInstance().schedule(this::loadRenderers);
    }

    @Override
    public void onDisable()
    {
        if (mode.getValue() == XrayMode.Opacity)
        {
            if (Environment.hasForge())
            {
                try
                {
                    Field field = Class
                        .forName("net.minecraftforge.common.ForgeModContainer",
                                    true, this.getClass().getClassLoader())
                        .getDeclaredField("forgeLightPipelineEnabled");

                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(null, this.lightPipeLine);
                    field.setAccessible(accessible);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (!FULL_BRIGHT.isEnabled())
            {
                mc.gameSettings.gammaSetting = 1.0F;
            }

            mc.renderChunksMany = true;
        }

        loadRenderers();
    }

    @Override
    protected SimpleRemovingSetting addSetting(String string)
    {
        SimpleRemovingSetting s = super.addSetting(string);
        if (s != null)
        {
            loadRenderers();
        }

        return s;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting)
    {
        Setting<?> s = super.unregister(setting);
        if (s != null)
        {
            loadRenderers();
        }

        return s;
    }

    public boolean shouldRender(Block block)
    {
        return isValid(block.getLocalizedName());
    }

    public XrayMode getMode()
    {
        return mode.getValue();
    }

    public int getOpacity()
    {
        return opacity.getValue();
    }

    public void loadRenderers()
    {
        if (mc.world != null
                && mc.player != null
                && mc.renderGlobal != null
                && mc.gameSettings != null)
        {
            WorldRenderUtil.reload(soft.getValue());
        }
    }

}
