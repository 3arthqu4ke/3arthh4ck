package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<BlockLag, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (!module.instantAttack.getValue()
            || event.getPacket().getType() != 51
            || mc.world == null
            || Managers.SWITCH.getLastSwitch() > module.cooldown.getValue()
            || !KeyBoardUtil.isKeyDown(module.getBind()) && !module.isEnabled()
            || DamageUtil.isWeaknessed()
            || mc.world.getBlockState(PositionUtil.getPosition(
                            RotationUtil.getRotationPlayer()).up(2))
                       .getMaterial()
                       .blocksMovement())
        {
            return;
        }

        EntityPlayerSP player = mc.player;
        if (player != null)
        {
            BlockPos pos = PositionUtil.getPosition(player);
            if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            {
                return;
            }

            EntityEnderCrystal crystal = new EntityEnderCrystal(mc.world,
                                                event.getPacket().getX(),
                                                event.getPacket().getY(),
                                                event.getPacket().getZ());
            if (crystal.getEntityBoundingBox()
                       .intersects(new AxisAlignedBB(pos)))
            {
                float damage = DamageUtil.calculate(crystal);
                if (module.pop.getValue()
                              .shouldPop(damage, module.popTime.getValue()))
                {
                    PacketUtil.attack(event.getPacket().getEntityID());
                }
            }
        }
    }

}
