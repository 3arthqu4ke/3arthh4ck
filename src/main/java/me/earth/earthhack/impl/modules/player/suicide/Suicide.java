package me.earth.earthhack.impl.modules.player.suicide;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.Set;

// TODO: instead of making this module place rewrite AutoCrystal slightly?
//  just need to redirect the parts where we check the suicide setting etc
public class Suicide extends DisablingModule
{
    protected final Setting<SuicideMode> mode =
        register(new EnumSetting<>("Mode", SuicideMode.Command));
    protected final Setting<Boolean> armor =
        register(new BooleanSetting("Armor", true));
    protected final Setting<Boolean> offhand =
        register(new BooleanSetting("Offhand", true));
    protected final Setting<Boolean> throwAwayTotem =
        register(new BooleanSetting("ThrowAwayTotem", true));
    protected final Setting<Integer> throwDelay =
        register(new NumberSetting<>("Throw-Delay", 500, 0, 1000));
    protected final Setting<Boolean> ask =
        register(new BooleanSetting("Ask", true));
    protected final Setting<Boolean> newVer =
        register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities =
        register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Float> breakRange =
        register(new NumberSetting<>("BreakRange", 6.0f, 0.0f, 6.0f));
    protected final Setting<Float> placeRange =
        register(new NumberSetting<>("PlaceRange", 5.25f, 0.0f, 6.0f));
    protected final Setting<Integer> placeDelay =
        register(new NumberSetting<>("PlaceDelay", 50, 0, 500));
    protected final Setting<Float> trace =
        register(new NumberSetting<>("RayTrace", 3.0f, 0.0f, 6.0f));
    protected final Setting<Integer> breakDelay =
        register(new NumberSetting<>("BreakDelay", 50, 0, 500));
    protected final Setting<Boolean> instant =
        register(new BooleanSetting("Instant", true));
    protected final Setting<Float> minInstant =
        register(new NumberSetting<>("Min-Instant", 6.0f, 0.0f, 36.0f));
    protected final Setting<Boolean> instantCalc =
        register(new BooleanSetting("Instant-Calc", false));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> silent =
        register(new BooleanSetting("Silent", false));

    protected final Set<BlockPos> placed = Sets.newConcurrentHashSet();
    protected final StopWatch placeTimer = new StopWatch();
    protected final StopWatch breakTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected boolean displaying;
    protected Entity crystal;
    protected RayTraceResult result;
    protected BlockPos pos;

    public Suicide()
    {
        super("Suicide", Category.Player);
        SimpleData data = new SimpleData(this, "Kills you.");
        data.register(mode, "-Command sends a /kill command" +
                "\n-AutoCrystal makes the AutoCrystal target you.");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        pos = null;
        placed.clear();
        if (ask.getValue())
        {
            displaying = true;
            GuiScreen current = mc.currentScreen;
            mc.displayGuiScreen(new YesNoNonPausing((r, id) ->
            {
                mc.displayGuiScreen(current);
                if (r)
                {
                    displaying = false;
                }
                else
                {
                    this.disable();
                }
            },
                TextColor.RED + "Do you want to kill yourself? (recommended)",
                "If you don't want to get asked again," +
                        " turn off the \"Ask\" Setting.",
                1337));
            return;
        }

        displaying = false;
        if (mode.getValue() == SuicideMode.Command)
        {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            this.disable();
        }
    }

    public boolean shouldTakeOffArmor()
    {
        return this.isEnabled()
                && !displaying
                && mode.getValue() != SuicideMode.Command
                && armor.getValue();
    }

    public boolean deactivateOffhand()
    {
        return this.isEnabled()
                && !displaying
                && mode.getValue() != SuicideMode.Command
                && offhand.getValue();
    }

}
