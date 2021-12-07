package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<EntitySpeed, TickEvent>
{
    private static final ModuleCache<BoatFly> BOAT_FLY =
            Caches.getModule(BoatFly.class);

    public ListenerTick(EntitySpeed module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe())
        {
            return;
        }

        Entity riding = mc.player.getRidingEntity();
        if (riding == null)
        {
            return;
        }

        double cosYaw =
                Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0f));
        double sinYaw =
                Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0f));

        BlockPos pos   = new BlockPos(mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw,
                                      mc.player.posY,
                                      mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        BlockPos down  = new BlockPos(mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw,
                                      mc.player.posY - 1.0,
                                      mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        if (!riding.onGround
                && !mc.world.getBlockState(pos).getMaterial().blocksMovement()
                && !mc.world.getBlockState(down).getMaterial().blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        pos = new BlockPos(mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw,
                           mc.player.posY,
                           mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        if (mc.world.getBlockState(pos).getMaterial().blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        pos = new BlockPos(mc.player.posX + cosYaw + 0.0 * sinYaw,
                           mc.player.posY + 1.0,
                           mc.player.posZ + (sinYaw - 0.0 * cosYaw));
        if (mc.world.getBlockState(pos).getMaterial().blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        if (mc.player.movementInput.jump)
        {
            module.jumpTimer.reset();
        }

        if (module.stuckTimer.passed(module.stuckTime.getValue())
                || !module.noStuck.getValue())
        {
            if (!riding.isInWater()
                    && !BOAT_FLY.isEnabled()
                    && !mc.player.movementInput.jump
                    && module.jumpTimer.passed(1000)
                    && !PositionUtil.inLiquid())
            {
                if (riding.onGround)
                {
                    riding.motionY = 0.4;
                }

                riding.motionY = -0.4;
            }

            EntitySpeed.strafe(module.speed.getValue());
            if (module.resetStuck.getValue())
            {
                module.stuckTimer.reset();
            }
        }
    }

}
