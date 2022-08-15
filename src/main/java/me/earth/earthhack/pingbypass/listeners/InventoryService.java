package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SOpenInventory;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;

public class InventoryService extends SubscriberImpl implements Globals {
    private static final ModuleCache<PingBypassModule> MODULE =
        Caches.getModule(PingBypassModule.class);

    private boolean open = false;

    public InventoryService() {
        this.listeners.add(new LambdaListener<>(GuiScreenEvent.class, e -> {
            EntityPlayerSP player = mc.player;
            if (player == null || !MODULE.isEnabled() || MODULE.get().isOld()) {
                open = false;
                return;
            }

            if (e.getScreen() instanceof GuiInventory
                && !e.isCancelled()
                && ((GuiInventory) e.getScreen()).inventorySlots == player.inventoryContainer) {
                open = true;
                player.connection.sendPacket(new C2SOpenInventory(true));
            } else if (open) {
                open = false;
                player.connection.sendPacket(new C2SOpenInventory(false));
            }
        }));
    }

}
