package me.earth.earthhack.impl.core.mixins.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiContainer.class)
public interface IGuiContainer
{
    @Accessor("hoveredSlot")
    Slot getHoveredSlot();

}
