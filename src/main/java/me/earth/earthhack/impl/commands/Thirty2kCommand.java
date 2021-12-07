package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static me.earth.earthhack.impl.util.thread.EnchantmentUtil.addEnchantment;

public class Thirty2kCommand extends AbstractStackCommand
{
    public Thirty2kCommand()
    {
        super("32k", "32k");
        CommandDescriptions.register(this, "Gives you a 32k sword.");
    }

    @Override
    protected ItemStack getStack(String[] args)
    {
        ItemStack s = new ItemStack(Items.DIAMOND_SWORD);
        s.setStackDisplayName("3\u00B2arthbl4de");
        s.setCount(64);

        addEnchantment(s, 16, Short.MAX_VALUE); // Sharpness   32767
        addEnchantment(s, 19, 10);              // Knockback   10
        addEnchantment(s, 20, Short.MAX_VALUE); // Fire Aspect 32767
        addEnchantment(s, 21, 10);              // Looting     10
        addEnchantment(s, 22, 3);               // Sweeping    3
        addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
        addEnchantment(s, 70, 1);               // Mending
        addEnchantment(s, 71, 1);               // Curse of Vanishing

        return s;
    }

}
