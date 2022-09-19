package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

final class ListenerTeleport
    extends ModuleListener<Surround, PacketEvent.Post<CPacketConfirmTeleport>>
{
    public ListenerTeleport(Surround module) {
        super(module, PacketEvent.Post.class, CPacketConfirmTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketConfirmTeleport> event) {
        EntityPlayerSP player = mc.player;
        if (player != null
            && module.teleport.getValue()
            && !module.blockTeleporting) {
            module.startPos = module.getPlayerPos();
        }
    }

}
