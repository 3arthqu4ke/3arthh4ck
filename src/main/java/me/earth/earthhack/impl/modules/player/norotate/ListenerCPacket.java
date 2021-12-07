package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket extends CPacketPlayerListener implements Globals
{
    private final NoRotate module;

    public ListenerCPacket(NoRotate module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        onPacket(event.getPacket());
    }

    private void onPacket(CPacketPlayer packet)
    {
        if (module.noSpoof.getValue()
            && !Managers.ROTATION.isBlocking()
            && (ItemUtil.isThrowable(mc.player.getActiveItemStack().getItem())
                || mc.player.getActiveItemStack().getItem() instanceof ItemBow)
            && packet.getYaw(mc.player.rotationYaw) != mc.player.rotationYaw)
        {
            ((ICPacketPlayer) packet).setYaw(mc.player.rotationYaw);
            ((ICPacketPlayer) packet).setPitch(mc.player.rotationPitch);
        }
    }

}
