package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;

final class ListenerUpdate extends ModuleListener<Phase, UpdateEvent>
{
    public ListenerUpdate(Phase module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.mode.getValue() == PhaseMode.NoClip)
        {
            mc.player.noClip       = true;
            mc.player.onGround     = false;
            mc.player.fallDistance = 0;
        }

        if (module.mode.getValue() == PhaseMode.Constantiam
                && MovementUtil.isMoving()
                && module.constTeleport.getValue()
                && module.isPhasing()) {
            double multiplier = module.constSpeed.getValue();
            double mx = -Math.sin(Math.toRadians(this.mc.player.rotationYaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.rotationYaw));
            double x = (double) mc.player.movementInput.moveForward * multiplier * mx + (double) mc.player.movementInput.moveStrafe * multiplier * mz;
            double z = (double) mc.player.movementInput.moveForward * multiplier * mz - (double) mc.player.movementInput.moveStrafe * multiplier * mx;
            this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
        }

        if (module.mode.getValue() == PhaseMode.ConstantiamNew) {
            double multiplier = 0.3;
            double mx = -Math.sin(Math.toRadians(this.mc.player.rotationYaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.rotationYaw));
            double x = (double)mc.player.movementInput.moveForward * multiplier * mx + (double)mc.player.movementInput.moveStrafe * multiplier * mz;
            double z = (double)mc.player.movementInput.moveForward * multiplier * mz - (double)mc.player.movementInput.moveStrafe * multiplier * mx;
            if (mc.player.collidedHorizontally && !this.mc.player.isOnLadder()) {
                PacketUtil.doPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z, false);
                for (int i = 1; i < 10; ++i) {
                    PacketUtil.doPosition(mc.player.posX,8.988465674311579E307, mc.player.posZ, false);
                }
                this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
            }
        }
    }

}
