package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerStopUsingItem extends ModuleListener<BowKiller, PacketEvent.Send<CPacketPlayerDigging>> {

    public ListenerStopUsingItem(BowKiller module) {
        super(module, PacketEvent.Send.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event) {
        if (!mc.player.collidedVertically)
            return;
        if (event.getPacket().getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM
                && mc.player.getActiveItemStack().getItem() == Items.BOW && module.blockUnder) {
            module.cancelling = false;
            if (module.packetsSent >= module.runs.getValue() * 2
                    || module.always.getValue()) {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);
                if (module.cancelRotate.getValue()
                        && (mc.player.rotationYaw
                        != Managers.ROTATION.getServerYaw()
                        || mc.player.rotationPitch
                        != Managers.ROTATION.getServerPitch())) {
                    PacketUtil.doRotation(mc.player.rotationYaw,
                            mc.player.rotationPitch,
                            true);
                }

                for (int i = 0; i < module.runs.getValue() + module.buffer.getValue(); i++) {
                    if (i != 0 && i % module.interval.getValue() == 0) {
                        int id = Managers.POSITION.getTeleportID();
                        for (int j = 0; j < module.teleports.getValue(); j++) {
                            mc.player.connection.sendPacket(new CPacketConfirmTeleport(++id));
                        }
                    }

                    double[] dir = MovementUtil.strafe(0.001);

                    if (module.rotate.getValue()) {
                        module.target = module.findTarget();
                        if (module.target != null) {
                            float[] rotations
                                    = module.rotationSmoother
                                    .getRotations(RotationUtil.getRotationPlayer(),
                                            module.target,
                                            module.height.getValue(),
                                            module.soft.getValue());
                            if (rotations != null) {
                                PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] : 0), mc.player.posY + 0.00000000000013, mc.player.posZ + (module.move.getValue() ? dir[1] : 0), rotations[0], rotations[1], true); // onground true
                                PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.posY + 0.00000000000027, mc.player.posZ + (module.move.getValue() ? dir[1] * 2 : 0), rotations[0], rotations[1], false); // onground false, jump
                            }
                        } else {
                            PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] : 0), mc.player.posY + 0.00000000000013, mc.player.posZ + (module.move.getValue() ? dir[1] : 0), mc.player.rotationYaw, mc.player.rotationPitch, true); // onground true
                            PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.posY + 0.00000000000027, mc.player.posZ + (module.move.getValue() ? dir[1] * 2 : 0), mc.player.rotationYaw, mc.player.rotationPitch, false); // onground false, jump
                        }
                    } else {
                        PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] : 0), mc.player.posY + 0.00000000000013, mc.player.posZ + (module.move.getValue() ? dir[1] : 0), mc.player.rotationYaw, mc.player.rotationPitch, true); // onground true
                        PacketUtil.doPosRotNoEvent(mc.player.posX + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.posY + 0.00000000000027, mc.player.posZ + (module.move.getValue() ? dir[1] * 2 : 0), mc.player.rotationYaw, mc.player.rotationPitch, false); // onground false, jump
                    }
                }
            }

            module.packetsSent = 0;
        }
    }

}