package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
            ModuleListener<PistonAura, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(PistonAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (module.stage == PistonStage.BREAK
                && module.current != null
                && module.breakTimer.passed(module.breakDelay.getValue())
                && Managers.SWITCH.getLastSwitch() > module.coolDown.getValue())
        {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51)
            {
                BlockPos pos = new BlockPos(packet.getX(),
                                            packet.getY(),
                                            packet.getZ());
                try
                {
                    if (module.current.getCrystalPos().equals(pos.down())
                            || module.current.getStartPos().equals(pos.down())
                            && (module.rotate.getValue() == Rotate.None
                                || RotationUtil.isLegit(pos))
                                || RotationUtil.isLegit(pos.up()))
                    {
                        module.entityId = packet.getEntityID();
                        if (!module.instant.getValue()
                                || !module.explode.getValue())
                        {
                            return;
                        }

                        //noinspection ConstantConditions
                        ICPacketUseEntity useEntity =
                                (ICPacketUseEntity) new CPacketUseEntity();
                        useEntity.setAction(CPacketUseEntity.Action.ATTACK);
                        useEntity.setEntityId(packet.getEntityID());

                        mc.player.connection.sendPacket((Packet<?>) useEntity);
                        mc.player.connection.sendPacket(
                                new CPacketAnimation(EnumHand.MAIN_HAND));

                        module.breakTimer.reset();
                    }
                }
                catch (Exception ignored)
                {
                    /* module.current could've been set to null */
                }
            }
        }
    }

}
