package me.earth.earthhack.impl.core.mixins.gui.util;

import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiDisconnected.class)
public interface IGuiDisconnected
{
    @Accessor(value = "parentScreen")
    GuiScreen getParentScreen();

    @Accessor(value = "reason")
    String getReason();

    @Accessor(value = "message")
    ITextComponent getMessage();

    @Accessor(value = "multilineMessage")
    List<String> getMultilineMessage();
}
