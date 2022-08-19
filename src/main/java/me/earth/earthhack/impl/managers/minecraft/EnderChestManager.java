package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.SendListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class EnderChestManager extends SubscriberImpl implements Globals
{

    private final Map<Integer, ItemStack> stacks = new ConcurrentHashMap<>();
    private final StopWatch timer = new StopWatch();
    private boolean shouldCache;
    private boolean hasInitialized;

    public EnderChestManager()
    {
        this.listeners.add(new EventListener<WorldClientEvent>(WorldClientEvent.class)
        {
            @Override
            public void invoke(WorldClientEvent event)
            {
                stacks.clear();
                shouldCache = false;
                hasInitialized = false;
            }
        });
        this.listeners.add(new SendListener<>(CPacketPlayerTryUseItemOnBlock.class, event ->
        {
            CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
            if (mc.world.getBlockState(packet.getPos()).getBlock() == Blocks.ENDER_CHEST
                    && !Managers.ACTION.isSneaking())
            {
                shouldCache = true;
                timer.reset();
            }
        }));
        this.listeners.add(new EventListener<GuiScreenEvent<?>>(GuiScreenEvent.class)
        {
            @Override
            public void invoke(GuiScreenEvent<?> event)
            {
                GuiScreen currentScreen = mc.currentScreen;
                if (event.getScreen() == null
                        && currentScreen instanceof GuiChest
                        && shouldCache)
                {
                    GuiChest container = (GuiChest) currentScreen;
                    for (int i = 0; i < 28; i++)
                    {
                        ItemStack stack = container.inventorySlots.getInventory().get(i);
                        stacks.put(i, stack);
                    }
                    hasInitialized = true;
                    shouldCache = false;
                }
            }
        });
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                if (shouldCache
                        && timer.passed(1000)
                        && mc.currentScreen == null)
                {
                    // if gui hasn't opened in 1 second, something probably went wrong, so reset and prepare to cache again
                    shouldCache = false;
                }
            }
        });
    }

    public ItemStack getStackInSlot(int slot)
    {
        if (!hasInitialized) return null;
        return stacks.get(slot);
    }

    public boolean has(Predicate<ItemStack> stackPredicate)
    {
        if (stacks.isEmpty()) return false;
        return stacks.values().stream().anyMatch(stackPredicate);
    }

    public boolean hasItem(Item item)
    {
        if (stacks.isEmpty()) return false;
        return stacks.values().stream().anyMatch(stack -> stack.getItem() == item);
    }

    public boolean hasInitialized()
    {
        return hasInitialized;
    }

}
