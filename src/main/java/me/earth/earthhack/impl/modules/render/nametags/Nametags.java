package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class Nametags extends Module
{
    protected final Setting<Boolean> twoD     =
            register(new BooleanSetting("2D", false));
    protected final Setting<Boolean> health     =
            register(new BooleanSetting("Health", true));
    protected final Setting<Boolean> ping       =
            register(new BooleanSetting("Ping", true));
    protected final Setting<Boolean> id         =
            register(new BooleanSetting("Id", false));
    protected final Setting<Boolean> itemStack  =
            register(new BooleanSetting("StackName", false));
    protected final Setting<Boolean> armor      =
            register(new BooleanSetting("Armor", true));
    protected final Setting<Boolean> gameMode   =
            register(new BooleanSetting("GameMode", false));
    protected final Setting<Boolean> durability =
            register(new BooleanSetting("Durability", true));
    protected final Setting<Boolean> invisibles =
            register(new BooleanSetting("Invisibles", false));
    protected final Setting<Boolean> pops       =
            register(new BooleanSetting("Pops", true));
    protected final Setting<Boolean> burrow     =
            register(new BooleanSetting("Burrow", true));
    protected final Setting<Boolean> fov        =
            register(new BooleanSetting("Fov", true));
    protected final Setting<Boolean> sneak      =
            register(new BooleanSetting("Sneak", true));
    protected final Setting<Float> scale        = // TODO: divide by 100
            register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));
    protected final Setting<Integer> delay      =
            register(new NumberSetting<>("Delay", 16, 0, 100));
    protected final Setting<Boolean> debug      =
            register(new BooleanSetting("Debug", false));
    protected final Setting<Boolean> media      =
            register(new BooleanSetting("Media", true));
    protected final Setting<Boolean> phase      =
            register(new BooleanSetting("Phase", false));
    protected final Setting<PushMode> pushMode =
            register(new EnumSetting<>("PhasePushDetect", PushMode.None))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> multiThread =
            register(new BooleanSetting("MultiThread", true));
    protected final Setting<Boolean> motion =
            register(new BooleanSetting("MotionXYZ", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> motionKpH =
            register(new BooleanSetting("MotionKpH", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> close        =
            register(new BooleanSetting("Close", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> withDistance        =
            register(new BooleanSetting("WithDistance", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> distance        =
            register(new NumberSetting<>("Distance", 300.0, 0.0, 1000.0))
                .setComplexity(Complexity.Expert);

    protected List<Nametag> nametags = new ArrayList<>();
    protected final StopWatch timer = new StopWatch();

    public Nametags()
    {
        super("Nametags", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRender2D(this));
        this.setData(new NametagsData(this));
    }

    protected void updateNametags()
    {
        if (timer.passed(delay.getValue()))
        {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            if (players == null)
            {
                return;
            }

            SafeRunnable runnable = () ->
            {
                List<Nametag> nametags = new ArrayList<>(players.size());
                for (EntityPlayer player : players)
                {
                    if (player != null
                            && !player.isDead
                            && !player.equals(mc.player)
                            && !(player instanceof IEntityNoNametag))
                    {
                        nametags.add(new Nametag(this, player));
                    }
                }

                this.nametags = nametags;
            };

            if (multiThread.getValue())
            {
                Managers.THREAD.submit(runnable);
            }
            else
            {
                runnable.run();
            }

            timer.reset();
        }
    }

    protected int getFontOffset(int enchHeight)
    {
        int armorOffset = armor.getValue() ? -26 : -27;
        if (enchHeight > 4)
        {
            armorOffset -= (enchHeight - 4) * 8;
        }

        return armorOffset;
    }

}
