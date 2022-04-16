package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.chat.util.LoggerMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.gui.ChatLine;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Chat extends Module
{
    protected final Setting<Boolean> noScroll =
            register(new BooleanSetting("AntiScroll", true));
    protected final Setting<Boolean> timeStamps =
            register(new BooleanSetting("TimeStamps", false));
    public final Setting<Boolean> animated =
            register(new BooleanSetting("Animated", false));
    public final Setting<Integer> time =
            register(new NumberSetting<>("AnimationTime", 200, 1, 500));
    protected final Setting<Boolean> autoQMain =
            register(new BooleanSetting("AutoQMain", false));
    protected final Setting<Integer> qDelay =
            register(new NumberSetting<>("Q-Delay", 5000, 1, 10000));
    protected final Setting<String> message =
            register(new StringSetting("Q-Message", "/queue main"));
    protected final Setting<LoggerMode> log =
            register(new EnumSetting<>("Log", LoggerMode.Normal));

    protected final Queue<ChatEvent.Send> events = new ConcurrentLinkedQueue<>();
    public final Map<ChatLine, TimeAnimation> animationMap = new HashMap<>();
    protected final StopWatch timer = new StopWatch();
    protected boolean cleared;

    public Chat()
    {
        super("Chat", Category.Misc);
        register(new BooleanSetting("Clean", false));
        register(new BooleanSetting("Infinite", false));
        register(new ColorSetting("TimeStampsColor", Color.WHITE));
        register(new EnumSetting<>("Rainbow", Rainbow.Horizontal));
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerChatLog(this));
        this.noScroll.addObserver(event ->
        {
            if (!event.getValue())
            {
                Scheduler.getInstance().schedule(this::clearNoScroll);
            }
        });

        register(new BooleanSetting("Clear", false)).addObserver(e ->
        {
            e.setCancelled(true);
            if (mc.ingameGUI != null)
            {
                mc.ingameGUI.getChatGUI().clearChatMessages(true);
            }
        });

        this.setData(new ChatData(this));
    }

    public enum Rainbow {
        None,
        Horizontal,
        Vertical
    }

    @Override
    public void onDisable()
    {
        clearNoScroll();
    }

    public void clearNoScroll()
    {
        if (mc.ingameGUI != null)
        {
            CollectionUtil.emptyQueue(events, ChatEvent.Send::invoke);
        }
        else
        {
            events.clear();
        }

        cleared = true;
    }

}
