package me.earth.earthhack.impl.core.ducks.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Duck interface for {@link net.minecraft.tileentity.TileEntityShulkerBox}.
 */
public interface ITileEntityShulkerBox
{
    NonNullList<ItemStack> getItems();
}
