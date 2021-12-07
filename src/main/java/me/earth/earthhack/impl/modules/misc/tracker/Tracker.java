package me.earth.earthhack.impl.modules.misc.tracker;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Tracker extends DisablingModule implements CustomCommandModule
{
    protected final Setting<Boolean> autoEnable =
        register(new BooleanSetting("Auto-Enable", false));
    protected final Setting<Boolean> only1v1 =
        register(new BooleanSetting("1v1-Only", true));

    protected final Set<BlockPos> placed = new ConcurrentSet<>();
    protected final StopWatch timer = new StopWatch();

    protected final AtomicInteger awaitingExp = new AtomicInteger();
    protected final AtomicInteger crystals = new AtomicInteger();
    protected final AtomicInteger exp = new AtomicInteger();

    protected EntityPlayer trackedPlayer;
    protected boolean awaiting;
    protected int crystalStacks;
    protected int expStacks;

    public Tracker()
    {
        super("Tracker", Category.Misc);
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerUseItemOnBlock(this));
        Bus.EVENT_BUS.register(new ListenerChat(this));
        Bus.EVENT_BUS.register(new ListenerTick(this));
        SimpleData data = new SimpleData(this,
            "Tracks the items players use. Only recommended in a 1v1!");
        data.register(autoEnable,
                "Enables automatically when a duel starts.");
        data.register(only1v1, "Automatically disables when there's more than" +
                " one player in render distance.");
        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        awaiting = false;
        trackedPlayer = null;

        awaitingExp.set(0);
        crystals.set(0);
        exp.set(0);

        crystalStacks = 0;
        expStacks = 0;
    }

    @Override
    public String getDisplayInfo()
    {
        return trackedPlayer == null ? null : trackedPlayer.getName();
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length == 1 && this.isEnabled())
        {
            EntityPlayer tracked = trackedPlayer;
            if (tracked != null)
            {
                // schedule twice so the message comes after the settings
                Scheduler.getInstance().schedule(() ->
                    Scheduler.getInstance().schedule(() ->
                    {
                        int c = crystals.get();
                        int e = exp.get();

                        StringBuilder builder = new StringBuilder()
                                .append(tracked.getName())
                                .append(TextColor.LIGHT_PURPLE)
                                .append(" has used ")
                                .append(TextColor.WHITE)
                                .append(c)
                                .append(TextColor.LIGHT_PURPLE)
                                .append(" (")
                                .append(TextColor.WHITE);

                        if (c % 64 == 0)
                        {
                            builder.append(c / 64);
                        }
                        else
                        {
                            builder.append(MathUtil.round(c / 64.0, 1));
                        }

                        builder.append(TextColor.LIGHT_PURPLE)
                               .append(") crystals and ")
                               .append(TextColor.WHITE)
                               .append(e)
                               .append(TextColor.LIGHT_PURPLE)
                               .append(" (")
                               .append(TextColor.WHITE);

                        if (e % 64 == 0)
                        {
                            builder.append(e / 64);
                        }
                        else
                        {
                            builder.append(MathUtil.round(e / 64.0, 1));
                        }

                        builder.append(TextColor.LIGHT_PURPLE)
                               .append(") bottles of experience.");

                        ChatUtil.sendMessage(builder.toString());
                    }));
            }
        }

        return false;
    }

}
