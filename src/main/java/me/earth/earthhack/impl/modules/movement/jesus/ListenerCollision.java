package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

final class ListenerCollision extends ModuleListener<Jesus, CollisionEvent>
{
    public ListenerCollision(Jesus module)
    {
        super(module, CollisionEvent.class);
    }

    @Override
    public void invoke(CollisionEvent event)
    {
        Entity entity;
        if (event.getEntity() != null
            && mc.player != null
            && (event.getEntity().equals(mc.player)
                    && module.mode.getValue() != JesusMode.Dolphin
                || event.getEntity().getControllingPassenger() != null
                    && Objects.equals(
                            event.getEntity().getControllingPassenger(),
                            mc.player))
            && (entity = PositionUtil.getPositionEntity()) != null
            && event.getBlock() instanceof BlockLiquid
            && !mc.player.isSneaking()
            && entity.fallDistance < 3.0F
            && !PositionUtil.inLiquid()
            && PositionUtil.inLiquid(false)
            && PositionUtil.isAbove(event.getPos()))
        {
            BlockPos pos = event.getPos();
            event.setBB(new AxisAlignedBB(pos.getX(),
                                          pos.getY(),
                                          pos.getZ(),
                                          pos.getX() + 1,
                                          pos.getY() + 0.99,
                                          pos.getZ() + 1));
        }
    }

}
