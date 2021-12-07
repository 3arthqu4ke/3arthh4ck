package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackListener;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackingModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.Passable;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class HighJump extends ObbyListenerModule<ListenerObby>
        implements InstantAttackingModule
{
    protected final Setting<Boolean> scaffold;
    protected final Setting<Double> range;

    protected final Setting<Double> height    =
            register(new NumberSetting<>("Height", 0.42, 0.0, 1.0));
    protected final Setting<Boolean> onGround =
            register(new BooleanSetting("OnGround", true));
    protected final Setting<Boolean> onlySpecial =
            register(new BooleanSetting("OnlySpecial", false));
    protected final Setting<Boolean> explosions =
            register(new BooleanSetting("Explosions", false));
    protected final Setting<Boolean> velocity =
            register(new BooleanSetting("Velocity", false));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Jump-Delay", 250, 0, 1000));
    protected final Setting<Boolean> constant =
            register(new BooleanSetting("Constant", false));
    protected final Setting<Double> factor =
            register(new NumberSetting<>("Factor", 1.0, 0.0, 10.0));
    protected final Setting<Double> minY =
            register(new NumberSetting<>("MinY", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> cancelJump =
            register(new BooleanSetting("Cancel-Jump", false));
    protected final Setting<Integer> lagTime =
            register(new NumberSetting<>("LagTime", 1000, 0, 2500));
    protected final Setting<Boolean> resetAlways =
            register(new BooleanSetting("Reset-Always", false));
    protected final Setting<Double> alwaysY =
            register(new NumberSetting<>("Always-Y", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> addY =
            register(new BooleanSetting("Add-Y", false));
    protected final Setting<Double> addFactor =
            register(new NumberSetting<>("Add-Factor", 0.5, 0.1, 2.0));
    protected final Setting<Boolean> addJump =
            register(new BooleanSetting("Add-Jump", false));
    protected final Setting<Double> jumpFactor =
            register(new NumberSetting<>("Jump-Factor", 0.5, 0.1, 2.0));
    protected final Setting<Double> scaffoldY =
            register(new NumberSetting<>("Scaffold-Y", 0.0, 0.0, 2.0));
    protected final Setting<Double> scaffoldMaxY =
            register(new NumberSetting<>("Scaffold-Max-Y", 10.0, 0.0, 10.0));
    protected final Setting<Integer> scaffoldOffset =
            register(new NumberSetting<>("Scaffold-Offset", 2, 0, 3));
    protected final Setting<Boolean> instant =
            register(new BooleanSetting("Instant-Attack", true));
    // TODO: placeAsync

    protected final StopWatch timer = new StopWatch();
    protected double motionY;

    public HighJump()
    {
        super("HighJump", Category.Movement);
        this.listeners.clear(); // Remove DisablingModule listeners
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerVelocity(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new InstantAttackListener<>(this));

        this.scaffold = registerBefore(new BooleanSetting("Scaffold", false),
                                       this.blocks);
        this.range = registerAfter(new NumberSetting<>("Range", 5.25, 0.1, 6.0),
                                       this.scaffold);

        this.setData(new HighJumpData(this));
    }

    @Override
    protected boolean checkNull()
    {
        packets.clear();
        blocksPlaced = 0;
        return mc.player != null && mc.world != null;
    }

    @Override
    public String getDisplayInfo()
    {
        if (velocity.getValue() || explosions.getValue())
        {
            if (!Managers.NCP.passed(lagTime.getValue()))
            {
                return TextColor.RED + "Lag";
            }

            if (timer.passed(delay.getValue()))
            {
                return "0.00";
            }

            double y = MathUtil.round(motionY, 2);
            if (motionY < minY.getValue())
            {
                return TextColor.RED + y;
            }

            return TextColor.GREEN + y;
        }

        return null;
    }

    @Override
    protected void onEnable()
    {
        motionY = 0.0;
    }

    @Override
    protected ListenerObby createListener()
    {
        return new ListenerObby(this, 9);
    }

    public void addVelocity(double y)
    {
        if (timer.passed(delay.getValue()))
        {
            motionY = y;
            timer.reset();
        }
        else if (addY.getValue())
        {
            motionY += y * addFactor.getValue();
        }

        if (resetAlways.getValue() && y >= alwaysY.getValue())
        {
            timer.reset();
        }

        if (addJump.getValue()
                && !mc.player.onGround
                && mc.gameSettings.keyBindJump.isKeyDown())
        {
            mc.player.motionY += y * jumpFactor.getValue();
        }
    }

    @Override
    public boolean shouldAttack(EntityEnderCrystal entity)
    {
        if (!attack.getValue() || !instant.getValue())
        {
            return false;
        }

        BlockPos pos = PositionUtil.getPosition();
        for (int i = 0; i <= range.getValue(); i++)
        {
            if (entity.getEntityBoundingBox()
                      .intersects(new AxisAlignedBB(pos.down(i))))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Passable getTimer()
    {
        return attackTimer;
    }

    @Override
    public int getBreakDelay()
    {
        return breakDelay.getValue();
    }

    @Override
    public int getCooldown()
    {
        return cooldown.getValue();
    }

}
