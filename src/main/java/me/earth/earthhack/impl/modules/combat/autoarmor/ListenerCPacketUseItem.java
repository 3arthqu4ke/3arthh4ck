package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.SendListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

final class ListenerCPacketUseItem extends SendListener<CPacketPlayerTryUseItem> implements Globals
{
    public ListenerCPacketUseItem(AutoArmor module)
    {
        super(CPacketPlayerTryUseItem.class, 1000, p ->
        {
            EntityPlayerSP player = mc.player;
            if (player != null
                    && (player.getHeldItem(p.getPacket().getHand()).getItem() == Items.EXPERIENCE_BOTTLE
                        // TODO: thought this might be necessary because silent switch but is it really?
                        || p.getPacket().getHand() == EnumHand.MAIN_HAND
                            && player.inventory.getStackInSlot(player.inventory.currentItem).getItem()
                                == Items.EXPERIENCE_BOTTLE)
                    && module.isBlockingMending())
            {
                p.setCancelled(true);
            }
        });
    }

}
