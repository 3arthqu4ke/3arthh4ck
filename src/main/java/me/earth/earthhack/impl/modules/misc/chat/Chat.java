package me.earth.earthhack.impl.modules.misc.chat;

import io.netty.util.internal.ConcurrentSet;
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
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.ChatLine;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Chat extends Module
{
    // TODO: theres actually many more possible Lag messages???
    // TODO: Could we just preload the ResourceLocations in the FontRenderer?
    protected static final String LAG_MESSAGE =
        "\u0101\u0201\u0301\u0401\u0601\u0701\u0801\u0901\u0A01" +
            "\u0B01\u0E01\u0F01\u1001\u1101\u1201\u1301\u1401\u1501" +
            "\u1601\u1701\u1801\u1901\u1A01\u1B01\u1C01\u1D01\u1E01" +
            "\u1F01 \u2101\u2201\u2301\u2401\u2501\u2701\u2801\u2901" +
            "\u2A01\u2B01\u2C01\u2D01\u2E01\u2F01\u3001\u3101\u3201" +
            "\u3301\u3401\u3501\u3601\u3701\u3801\u3901\u3A01\u3B01" +
            "\u3C01\u3D01\u3E01\u3F01\u4001\u4101\u4201\u4301\u4401" +
            "\u4501\u4601\u4701\u4801\u4901\u4A01\u4B01\u4C01\u4D01" +
            "\u4E01\u4F01\u5001\u5101\u5201\u5301\u5401\u5501\u5601" +
            "\u5701\u5801\u5901\u5A01\u5B01\u5C01\u5D01\u5E01\u5F01" +
            "\u6001\u6101\u6201\u6301\u6401\u6501\u6601\u6701\u6801" +
            "\u6901\u6A01\u6B01\u6C01\u6D01\u6E01\u6F01\u7001\u7101" +
            "\u7201\u7301\u7401\u7501\u7601\u7701\u7801\u7901\u7A01" +
            "\u7B01\u7C01\u7D01\u7E01\u7F01\u8001\u8101\u8201\u8301" +
            "\u8401\u8501\u8601\u8701\u8801\u8901\u8A01\u8B01\u8C01" +
            "\u8D01\u8E01\u8F01\u9001\u9101\u9201\u9301\u9401\u9501" +
            "\u9601\u9701\u9801\u9901\u9A01\u9B01\u9C01\u9D01\u9E01" +
            "\u9F01\uA001\uA101\uA201\uA301\uA401\uA501\uA601\uA701" +
            "\uA801\uA901\uAA01\uAB01\uAC01\uAD01\uAE01\uAF01\uB001" +
            "\uB101\uB201\uB301\uB401\uB501\uB601\uB701\uB801\uB901" +
            "\uBA01\uBB01\uBC01\uBD01";

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
    protected final Setting<Boolean> load =
            register(new BooleanSetting("LoadPopLag", false));
    protected final Setting<Boolean> popMessage =
            register(new BooleanSetting("PopLag", false));
    protected final Setting<Integer> popLagDelay =
            register(new NumberSetting<>("PopLag-Delay", 2500, 1, 10000));

    protected final Set<String> sent = new ConcurrentSet<>();
    protected final Queue<ChatEvent.Send> events = new ConcurrentLinkedQueue<>();
    public final Map<ChatLine, TimeAnimation> animationMap = new HashMap<>();
    protected final StopWatch popLagTimer = new StopWatch();
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
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerChatLog(this));
        this.listeners.add(new ListenerPop(this));
        this.listeners.add(new ListenerLogout(this));
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

        this.load.addObserver(e -> {
            e.setCancelled(true);
            load.setValue(false, false);
            ChatUtil.sendMessage(LAG_MESSAGE);
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
        sent.clear();
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
