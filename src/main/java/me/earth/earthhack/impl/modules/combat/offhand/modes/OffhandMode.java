package me.earth.earthhack.impl.modules.combat.offhand.modes;

import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * OffhandMode for the {@link Offhand}.
 */
public class OffhandMode
{
    /** Offhand Mode for {@link Items#TOTEM_OF_UNDYING}. */
    public static final OffhandMode TOTEM =
            new OffhandMode(Items.TOTEM_OF_UNDYING, "Totem");
    /** Offhand Mode for {@link Items#GOLDEN_APPLE}. */
    public static final OffhandMode GAPPLE =
            new OffhandMode(Items.GOLDEN_APPLE, "Gapple");
    /** Offhand Mode for {@link Items#END_CRYSTAL}. */
    public static final OffhandMode CRYSTAL =
            new OffhandMode(Items.END_CRYSTAL, "Crystal");
    /** Offhand Mode for {@link Blocks#OBSIDIAN}. */
    public static final OffhandMode OBSIDIAN =
            new OffhandMode(Blocks.OBSIDIAN, "Obsidian");

    private final String name;
    private final Item item;

    public OffhandMode(Block block, String name)
    {
        this(Item.getItemFromBlock(block), name);
    }

    public OffhandMode(Item item, String name)
    {
        this.item = item;
        this.name = name;
    }

    public Item getItem()
    {
        return item;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return item == null ? 0 : item.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof OffhandMode)
        {
            return ((OffhandMode) o).item == this.item;
        }

        return false;
    }

}
