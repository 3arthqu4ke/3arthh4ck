package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerUseItem extends
        ModuleListener<ExpTweaks, PacketEvent.Send<CPacketPlayerTryUseItem>>
{
    private boolean sending = false;

    public ListenerUseItem(ExpTweaks module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event)
    {
        CPacketPlayerTryUseItem p = event.getPacket();
        if (sending
                || event.isCancelled()
                || mc.player.getHeldItem(p.getHand())
                            .getItem() != Items.EXPERIENCE_BOTTLE)
        {
            return;
        }

        if (module.simpleWasteStop.getValue() && module.isSimpleWasting()
                || module.wasteStop.getValue() && module.isWasting())
        {
            event.setCancelled(true);
            module.justCancelled = true;
            return;
        }

        int packets = module.isMiddleClick ? module.mcePackets.getValue()
                                           : module.expPackets.getValue();
        if (packets != 0
                && (module.packetsInLoot.getValue()
                    || mc.world
                         .getEntitiesWithinAABB(EntityItem.class,
                                    RotationUtil.getRotationPlayer()
                                                .getEntityBoundingBox())
                         .isEmpty()))
        {
            for (int i = 0; i < packets; i++)
            {
                sending = true; // This isn't really threadsafe...
                NetworkUtil.send(new CPacketPlayerTryUseItem(p.getHand()));
                sending = false;
            }
        }
    }

}
