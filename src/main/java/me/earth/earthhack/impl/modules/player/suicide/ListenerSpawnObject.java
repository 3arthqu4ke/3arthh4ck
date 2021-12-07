package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<Suicide, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(Suicide module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (event.getPacket().getType() != 51
            || !module.instant.getValue()
            || !module.breakTimer.passed(module.breakDelay.getValue())
            || module.mode.getValue() == SuicideMode.Command)
        {
            return;
        }

        EntityEnderCrystal crystal = new EntityEnderCrystal(mc.world,
                event.getPacket().getX(),
                event.getPacket().getY(),
                event.getPacket().getZ());

        if (RotationUtil.getRotationPlayer().getDistanceSq(crystal)
                >= MathUtil.square(module.breakRange.getValue())
            || RotationUtil.getRotationPlayer().getDistanceSq(crystal)
                >= MathUtil.square(module.trace.getValue())
                && !RotationUtil.getRotationPlayer().canEntityBeSeen(crystal)
            || module.rotate.getValue()
                    && !RotationUtil.isLegit(crystal, crystal))
        {
            return;
        }

        if (!module.instantCalc.getValue())
        {
            if (module.placed.remove(new BlockPos(crystal.posX,
                                                  crystal.posY - 1,
                                                  crystal.posZ)))
            {
                PacketUtil.attack(event.getPacket().getEntityID());
                module.breakTimer.reset();
            }

            return;
        }

        float damage = DamageUtil.calculate(crystal);
        if (damage > module.minInstant.getValue())
        {
            PacketUtil.attack(event.getPacket().getEntityID());
            module.breakTimer.reset();
        }
    }

}
