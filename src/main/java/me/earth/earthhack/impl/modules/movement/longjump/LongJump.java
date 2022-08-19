package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class LongJump extends DisablingModule
{
    protected final Setting<JumpMode> mode     =
            register(new EnumSetting<>("Mode", JumpMode.Normal));
    protected final Setting<Double> boost      =
            register(new NumberSetting<>("Boost", 4.5, 0.1, 20.0));
    protected final Setting<Boolean> noKick    =
            register(new BooleanSetting("AntiKick", true));
    protected final Setting<Bind> invalidBind    =
            register(new BindSetting("Invalid", Bind.fromKey(Keyboard.getKeyM())));

    protected int stage;
    protected int airTicks;
    protected int groundTicks;
    protected double speed;
    protected double distance;

    public LongJump()
    {
        super("LongJump", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPosLook(this));
        this.setData(new LongJumpData(this));
    }

    @Override
    protected void onEnable()
    {
        if (mc.player != null)
        {
            distance = MovementUtil.getDistance2D();
            speed    = MovementUtil.getSpeed();
        }

        stage       = 0;
        airTicks    = 0;
        groundTicks = 0;
    }

    @Override
    protected void onDisable()
    {
        Managers.TIMER.reset();
    }

    protected void invalidPacket()
    {
        updatePosition(0.0, Integer.MAX_VALUE, 0.0);
    }

    protected void updatePosition(double x, double y, double z)
    {
        mc.player.connection.sendPacket(
                new CPacketPlayer.Position(x, y, z, mc.player.onGround));
    }

    protected double getDistance(EntityPlayer player, double distance)
    {
        List<AxisAlignedBB> boundingBoxes = player.world
                .getCollisionBoxes(player,
                        player.getEntityBoundingBox().offset(0, -distance, 0));

        if (boundingBoxes.isEmpty())
        {
            return 0.0;
        }

        double y = 0.0;
        for (AxisAlignedBB boundingBox : boundingBoxes)
        {
            if (boundingBox.maxY > y)
            {
                y = boundingBox.maxY;
            }
        }

        return player.posY - y;
    }

}

