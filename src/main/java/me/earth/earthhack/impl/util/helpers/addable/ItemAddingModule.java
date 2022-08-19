package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.addable.setting.Removable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class ItemAddingModule<I, E extends Setting<I> & Removable>
        extends RegisteringModule<I, E>
{
    public ItemAddingModule(String name,
                            Category category,
                            Function<String, E> create,
                            Function<Setting<?>, String> settingDescription)
    {
        super(name,
                category,
                "Add_Block", "item/block",
                create,
                settingDescription);
    }

    @Override
    public String getInput(String input, boolean add)
    {
        if (add)
        {
            String itemName = getItemStartingWith(input);
            if (itemName != null)
            {
                return TextUtil.substring(itemName, input.length());
            }

            return "";
        }

        return super.getInput(input, false);
    }

    public boolean isStackValid(ItemStack stack)
    {
        return stack != null
                && isValid(stack.getItem().getItemStackDisplayName(stack));
    }

    public String getItemStartingWith(String name)
    {
        return getItemStartingWithDefault(name, i -> true);
    }

    /**
     * Utility Method. Scans {@link Item#REGISTRY} for
     * items whose name starts with the given one and
     * for which the given Predicate returns <tt>true</tt>.
     *
     * @param name the name the item should start with.
     * @param accept if we accept the item.
     * @return the full ItemName for an Item starting with the given one.
     */
    public static String getItemStartingWithDefault(String name,
                                                    Predicate<Item> accept)
    {
        Item item = getItemStartingWith(name, accept);
        if (item != null)
        {
            return item.getItemStackDisplayName(new ItemStack(item));
        }

        return null;
    }

    public static Item getItemStartingWith(String name, Predicate<Item> accept)
    {
        if (name == null)
        {
            return null;
        }

        name = name.toLowerCase();
        for (Item item : Item.REGISTRY)
        {
            String itemName = item.getItemStackDisplayName(new ItemStack(item));
            if (itemName.toLowerCase().startsWith(name) && accept.test(item))
            {
                return item;
            }
        }

        return null;
    }

}
