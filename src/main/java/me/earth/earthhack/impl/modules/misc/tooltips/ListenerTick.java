package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.core.mixins.gui.IGuiContainer;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

final class ListenerTick extends ModuleListener<ToolTips, TickEvent>
{
    public ListenerTick(ToolTips module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe())
        {
            if (mc.currentScreen instanceof IGuiContainer
                    && KeyBoardUtil.isKeyDown(module.peekBind))
            {
                Slot slot = ((IGuiContainer) mc.currentScreen)
                                               .getHoveredSlot();
                if (slot != null)
                {
                    ItemStack stack = slot.getStack();
                    if (stack.getItem() instanceof ItemShulkerBox)
                    {
                        module.displayInventory(stack, null);
                    }
                }
            }

            if (module.shulkerSpy.getValue())
            {
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (player != null && player.getHeldItemMainhand().getItem()
                            instanceof ItemShulkerBox)
                    {
                        if (!PlayerUtil.isFakePlayer(player)
                                && (module.own.getValue()
                                || !mc.player.equals(player)))
                        {
                            ItemStack stack = player.getHeldItemMainhand();
                            module.spiedPlayers
                                    .put(player.getName().toLowerCase(),
                                            new TimeStack(stack,
                                                    System.nanoTime()));
                        }
                    }
                }
            }
        }
    }

}
