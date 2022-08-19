package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

final class ListenerTryUseItem
        extends ModuleListener<NoSlowDown, PacketEvent.Post<CPacketPlayerTryUseItem>>
{
    public ListenerTryUseItem(NoSlowDown module)
    {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItem> event)
    {
        Item item = mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        if (module.superStrict.getValue() &&
                (item instanceof ItemFood
                        || item instanceof ItemBow
                        || item instanceof ItemPotion))
        {
            // int slot = mc.player.inventory.currentItem;
            // InventoryUtil.switchTo(mc.player.inventory.currentItem + 1);
            // InventoryUtil.switchTo(slot);
            NetworkUtil.send(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            // NetworkUtil.send(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN)); // ????????
        }
    }

}
