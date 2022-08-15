package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SActualPos;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;

public class CPacketPlayerService extends SubscriberImpl implements Globals {
    public CPacketPlayerService() {
        this.listeners.addAll(new CPacketPlayerListener(Integer.MIN_VALUE) {
            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
                CPacketPlayerService.this.onPacket();
            }

            @Override
            protected void onPosition(
                PacketEvent.Send<CPacketPlayer.Position> event) {
                CPacketPlayerService.this.onPacket();
            }

            @Override
            protected void onRotation(
                PacketEvent.Send<CPacketPlayer.Rotation> event) {
                CPacketPlayerService.this.onPacket();
            }

            @Override
            protected void onPositionRotation(
                PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
                CPacketPlayerService.this.onPacket();
            }
        }.getListeners());
    }

    public void onPacket() {
        EntityPlayerSP player = mc.player;
        if (PingBypassModule.CACHE.isEnabled()
            && !PingBypassModule.CACHE.get().isOld()
            && player != null) {
            player.connection.sendPacket(new C2SActualPos(
                player.posX, player.posY, player.posZ));
        }
    }

}
