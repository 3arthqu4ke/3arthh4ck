package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Objects;

/**
 * A command that gives you an ItemStack.
 */
public abstract class AbstractStackCommand extends Command implements Globals
{
    protected String stackName;

    public AbstractStackCommand(String name, String stackName)
    {
        this(new String[][]{{name}}, stackName);
    }

    public AbstractStackCommand(String[][] args, String stackName)
    {
        super(args);
        this.stackName = stackName;
    }

    /** The method that produces the ItemStack for this command. */
    protected abstract ItemStack getStack(String[] args);

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame for this command.");
            return;
        }

        boolean ghost = !mc.player.isCreative();
        boolean hotbar = true;
        int slot = InventoryUtil.findHotbarBlock(Blocks.AIR);

        if (slot == -1)
        {
            hotbar = false;
            slot = findBlockNoDrag(Blocks.AIR);
            if (slot == -1)
            {
                Scheduler.getInstance().schedule(() ->
                    mc.displayGuiScreen(
                        new YesNoNonPausing((result, id) ->
                        {
                            mc.displayGuiScreen(null);
                            if (result)
                            {
                                setSlot(args,
                                        mc.player.inventory.currentItem,
                                        true,
                                        ghost);
                            }
                        },
                        TextColor.RED + "Your inventory is full.",
                        "Should your MainHand Slot be replaced?",
                        1337)));
                return;
            }
        }

        setSlot(args, slot, hotbar, ghost);
    }

    private void setSlot(String[] args, int slot, boolean hotbar, boolean ghost)
    {
        if (mc.player == null)
        {
            return;
        }

        ItemStack stack = this.getStack(args);
        if (stack == null)
        {
            ChatUtil.sendMessage("<"
                                + this.getName()
                                + ">"
                                + TextColor.RED
                                + " An error occurred.");
            return;
        }

        if (hotbar)
        {
            slot = InventoryUtil.hotbarToInventory(slot);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);
        if (mc.player.isCreative())
        {
            mc.player.connection.sendPacket(
                    new CPacketCreativeInventoryAction(slot, stack));
        }
        else if (mc.isSingleplayer())
        {
            EntityPlayerMP player = Objects.requireNonNull(
                    mc.getIntegratedServer())
                      .getPlayerList()
                      .getPlayerByUUID(mc.player.getUniqueID());
            //noinspection ConstantConditions
            if (player != null)
            {
                player.inventoryContainer.putStackInSlot(slot, stack);
                ghost = false;
            }
        }

        ChatUtil.sendMessage(
                TextColor.GREEN
                + "Gave you a " + (ghost ? TextColor.RED + "(ghost) " : "")
                + TextColor.GREEN + stackName + ". It's in your "
                + TextColor.WHITE
                    + ((slot == 45
                        ?  "Offhand"
                        : (hotbar ? "Hotbar" : "Inventory")))
                + TextColor.GREEN + ".");
    }

    public static int findBlockNoDrag(Block block)
    {
        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = mc.player
                                .inventoryContainer
                                .getInventory()
                                .get(i);

            if (ItemUtil.areSame(stack, block))
            {
                return i;
            }
        }

        return -1;
    }

}
