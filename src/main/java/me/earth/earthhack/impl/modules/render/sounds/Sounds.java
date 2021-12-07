package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.audio.ISoundHandler;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.render.sounds.util.CoordLogger;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPages;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sounds extends RegisteringModule<Boolean, SimpleRemovingSetting>
{
    protected final Setting<SoundPages> pages =
        register(new EnumSetting<>("Page", SoundPages.ESP));

    protected final Setting<Boolean> render =
        register(new BooleanSetting("Render", true));
    protected final Setting<Boolean> custom =
        register(new BooleanSetting("Custom", true));
    protected final Setting<Boolean> packet =
        register(new BooleanSetting("Packet", true));
    protected final Setting<Boolean> client =
        register(new BooleanSetting("Client-Side", false));
    protected final ColorSetting color =
        register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Integer> remove =
        register(new NumberSetting<>("Remove", 750, 0, 20000));
    protected final Setting<Boolean> fade =
        register(new BooleanSetting("Fade", false));
    protected final Setting<Boolean> rect =
        register(new BooleanSetting("Rectangle", false));
    protected final Setting<Float> scale =
        register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));
    protected final Setting<Boolean> cancelled =
        register(new BooleanSetting("Cancelled", true));

    protected final Setting<CoordLogger> coordLogger =
        register(new EnumSetting<>("Coord-Logger", CoordLogger.Vanilla));
    protected final Setting<Boolean> chat =
        register(new BooleanSetting("Chat", false));
    protected final Setting<Boolean> thunder =
        register(new BooleanSetting("Thunder", false));
    protected final Setting<Boolean> dragon =
        register(new BooleanSetting("Dragon", false));
    protected final Setting<Boolean> wither =
        register(new BooleanSetting("Wither", false));
    protected final Setting<Boolean> portal =
        register(new BooleanSetting("Portal", false));
    protected final Setting<Boolean> slimes =
        register(new BooleanSetting("Slimes", false));

    protected final Map<SoundPosition, Long> sounds = new ConcurrentHashMap<>();

    public Sounds()
    {
        super("Sounds",
                Category.Render,
                "Add_Sound",
                "sound",
                SimpleRemovingSetting::new,
                s -> "White/Blacklist " + s.getName() + "sounds.");

        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerCustomSound(this));
        this.listeners.add(new ListenerClientSound(this));
        this.listeners.add(new ListenerEffect(this));
        this.listeners.add(new ListenerSpawnMob(this));

        new PageBuilder<>(this, pages)
            .addPage(v -> v == SoundPages.ESP, render, cancelled)
            .addPage(v -> v == SoundPages.CoordLogger, coordLogger, slimes)
            .register(Visibilities.VISIBILITY_MANAGER);

        super.listType.setValue(ListType.BlackList);
    }

    @Override
    public String getInput(String input, boolean add)
    {
        if (add)
        {
            String itemName = getSoundStartingWith(input);
            if (itemName != null)
            {
                return TextUtil.substring(itemName, input.length());
            }

            return "";
        }

        return super.getInput(input, false);
    }

    public void log(String s)
    {
        if (chat.getValue())
        {
            mc.addScheduledTask(() -> ChatUtil.sendMessage(s));
        }
        else
        {
            Earthhack.getLogger().info(s);
        }
    }

    public static String getSoundStartingWith(String prefix)
    {
        ISoundHandler handler = (ISoundHandler) mc.getSoundHandler();
        for (SoundEventAccessor soundEventAccessor : handler.getRegistry())
        {
            if (TextUtil.startsWith(soundEventAccessor.getLocation().toString(),
                                    prefix))
            {
                return soundEventAccessor.getLocation().toString();
            }

            ITextComponent component = soundEventAccessor.getSubtitle();
            if (component == null)
            {
                continue;
            }

            if (TextUtil.startsWith(component.getUnformattedComponentText(),
                                    prefix))
            {
                return component.getUnformattedComponentText();
            }
        }

        return null;
    }

}
