package me.earth.earthhack.impl.modules.client.pbteleport;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CLagBack;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends ModuleListener<PbTeleport, PacketEvent.Receive<SPacketPlayerPosLook>> {
    private static final ModuleCache<Surround> SURROUND =
        Caches.getModule(Surround.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG =
        Caches.getModule(BlockLag.class);

    public ListenerPosLook(PbTeleport module) {
        super(module, PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (PbTeleport.isActive()) {
            int id;
            synchronized (module.currentId) {
                id = module.currentId.incrementAndGet();
                module.blocking.set(true);
            }

            event.setCancelled(true);
            event.setPingBypassCancelled(true);
            PingBypass.sendPacket(event.getPacket());
            PingBypass.sendPacket(new S2CLagBack(id));
            EntityPlayerSP player = mc.player;
            if (module.async.getValue() && player != null) {
                PacketUtil.handlePosLook(event.getPacket(), player, false, false);
            } else {
                mc.addScheduledTask(() -> {
                    if (mc.player != null && module.blocking.get()) {
                        PacketUtil.handlePosLook(event.getPacket(), mc.player, false, false);
                    }
                });
            }

            mc.addScheduledTask(() -> {
                if (module.blockLag.getValue()) {
                    BLOCK_LAG.disable();
                    BLOCK_LAG.enable();
                }

                if (module.enableSurround.getValue()) {
                    SURROUND.disable();
                    SURROUND.enable();
                }
            });
        } else {
            module.blocking.set(false);
        }
    }

}
