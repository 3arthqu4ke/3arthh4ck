package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.text.translation.I18n;

import java.util.Objects;

public class EnchantCommand extends Command implements Globals
{
    public EnchantCommand()
    {
        super(new String[][]{{"enchant"}, {"level"}, {"enchantment"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please specify a level!");
            return;
        }

        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command!");
            return;
        }

        short level;

        try
        {
            level = (short) Integer.parseInt(args[1]);
        }
        catch (Exception e)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Could not parse level "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + "!");
            return;
        }

        ItemStack stack = mc.player.inventory.getCurrentItem();
        if (stack.isEmpty())
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be holding an item for this command!");
            return;
        }

        if (args.length > 2)
        {
            String conc = CommandUtil.concatenate(args, 2);
            Enchantment enchantment = getEnchantment(conc);
            if (enchantment == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "Could find Enchantment "
                        + TextColor.WHITE
                        + conc
                        + TextColor.RED
                        + "!");
                return;
            }

            stack.addEnchantment(enchantment, level);
            setStack(stack);
            return;
        }

        for (Enchantment enchantment : Enchantment.REGISTRY)
        {
            if (!enchantment.isCurse())
            {
                stack.addEnchantment(enchantment, level);
            }
        }

        setStack(stack);
    }

    private void setStack(ItemStack stack)
    {
        int slot = mc.player.inventory.currentItem + 36;
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
            }
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Not Creative and not Singleplayer: "
                    + "Enchantments are "
                    + TextColor.AQUA
                    + "ghost "
                    + TextColor.RED
                    + "enchantments!");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 1)
        {
            PossibleInputs inputs = PossibleInputs.empty();
            if (args.length == 2)
            {
                inputs.setRest(" <enchantment>");
                if (args[1].isEmpty())
                {
                    return inputs.setRest("<level>");
                }

                return inputs;
            }

            if (args[2].isEmpty())
            {
                return inputs.setRest("<enchantment>");
            }

            String conc = CommandUtil.concatenate(args, 2);
            String s = getEnchantmentStartingWith(conc);
            if (s != null)
            {
                inputs.setCompletion(TextUtil.substring(s, conc.length()));
            }

            return inputs;
        }

        return super.getPossibleInputs(args);
    }

    @SuppressWarnings("deprecation")
    public static String getEnchantmentStartingWith(String prefix)
    {
        Enchantment enchantment = getEnchantment(prefix);
        if (enchantment != null)
        {
            return I18n.translateToLocal(enchantment.getName());
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static Enchantment getEnchantment(String prefixIn)
    {
        String prefix = prefixIn.toLowerCase();
        for (Enchantment enchantment : Enchantment.REGISTRY)
        {
            String s = I18n.translateToLocal(enchantment.getName());
            if (s.toLowerCase().startsWith(prefix))
            {
                return enchantment;
            }
        }

        return null;
    }

}
