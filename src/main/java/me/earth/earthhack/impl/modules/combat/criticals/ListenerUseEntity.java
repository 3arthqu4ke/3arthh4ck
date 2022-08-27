package me.earth.earthhack.impl.modules.combat.criticals;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.Vec3d;

/**
 *  Optional offsets:
 * <p>
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y + 0.05, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y + 0.03, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y, pos.z, false))
 */
final class ListenerUseEntity extends
        ModuleListener<Criticals, PacketEvent.Send<CPacketUseEntity>>
{
    private static final ModuleCache<KillAura> KILL_AURA =
            Caches.getModule(KillAura.class);

    public ListenerUseEntity(Criticals module)
    {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event)
    {

        if (event.getPacket().getAction() == CPacketUseEntity.Action.ATTACK
                && mc.player.onGround
                && !mc.gameSettings.keyBindJump.isKeyDown()
                && !(mc.player.isInWater() || mc.player.isInLava())
                && module.timer.passed(module.delay.getValue())
                && !(module.movePause.getValue() && MovementUtil.isMoving()))
        {
            CPacketUseEntity packet = event.getPacket();
            Entity entity = ((ICPacketUseEntity) packet).getAttackedEntity();
            if (!module.noDesync.getValue()
                    || entity instanceof EntityLivingBase)
            {
                Vec3d vec = RotationUtil.getRotationPlayer()
                                        .getPositionVector();

                Vec3d pos = KILL_AURA.returnIfPresent(
                                k -> k.criticalCallback(vec),
                                vec);

                switch (module.mode.getValue())
                {
                    case Packet:
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y + 0.0625101,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y + 1.1E-5,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y,
                                pos.z,
                                false));
                        break;
                    case Bypass:
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y + 0.062600301692775,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y + 0.07260029960661,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y,
                                pos.z,
                                false));
                        PingBypass.sendToActualServer(
                            new CPacketPlayer.Position(
                                pos.x,
                                pos.y,
                                pos.z,
                                false));
                        break;
                    case Jump:
                        mc.player.jump();
                        break;
                    case MiniJump:
                        mc.player.jump();
                        mc.player.motionY /= 2.0;
                        break;
                    default:
                }

                module.timer.reset();
            }
        }
    }

}
