package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityStatus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CAsyncTotemPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashSet;
import java.util.Set;

final class ListenerTotem extends
        ModuleListener<Offhand, PacketEvent.Receive<SPacketEntityStatus>>
{
    private static final ModuleCache<Suicide> SUICIDE =
            Caches.getModule(Suicide.class);

    public ListenerTotem(Offhand module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event)
    {
        EntityPlayerSP player = mc.player;
        if (player == null
            || !InventoryUtil.validScreen()
            || !module.async.getValue()
            || event.getPacket().getOpCode() != 35
            || !module.timer.passed(module.delay.getValue())
            || player.getEntityId() !=
                ((ISPacketEntityStatus) event.getPacket()).getEntityId()
            || SUICIDE.returnIfPresent(Suicide::deactivateOffhand, false))
        {
            return;
        }

        int currentItem;

        try
        {
            Locks.PLACE_SWITCH_LOCK.lock();
            currentItem = mc.player.inventory.currentItem;
        }
        finally
        {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }

        int slot = InventoryUtil.hotbarToInventory(currentItem);
        ItemStack stack = mc.player.inventory.getStackInSlot(currentItem);
        if (stack.getItem() != Items.TOTEM_OF_UNDYING)
        {
            slot = 45;
            stack = mc.player.getHeldItemOffhand();
            if (stack.getItem() != Items.TOTEM_OF_UNDYING)
            {
                return;
            }
        }

        if (stack.getCount() - 1 > 0)
        {
            return;
        }

        Set<Integer> ignore = new HashSet<>();
        ignore.add(slot);
        int t = InventoryUtil.findItem(Items.TOTEM_OF_UNDYING, true, ignore);
        if (t == -1)
        {
            return;
        }

        final int finalSlot = slot;
        try {
            Locks.WINDOW_CLICK_LOCK.lock();
            if (InventoryUtil.get(t).getItem() == Items.TOTEM_OF_UNDYING)
            {
                InventoryUtil.put(finalSlot, ItemStack.EMPTY);
                if (PingBypass.isConnected()) {
                    PingBypass.sendPacket(new S2CAsyncTotemPacket(finalSlot));
                }

                if (t != -2)
                {
                    Managers.NCP.startMultiClick();
                    InventoryUtil.click(t);
                }

                InventoryUtil.click(finalSlot);

                if (t != 2)
                {
                    Managers.NCP.releaseMultiClick();
                }
            }
        } finally {
            Locks.WINDOW_CLICK_LOCK.unlock();
        }

        module.asyncSlot = slot;
        module.asyncTimer.reset();
        module.postWindowClick();
    }

}
