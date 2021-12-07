package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.function.Supplier;

/**
 * A duck interface for {@link TextComponentBase}.
 *
 * Allows you to "override" the final methods
 * {@link TextComponentBase#getFormattedText()} and
 * {@link TextComponentBase#getUnformattedText()}
 */
public interface ITextComponentBase
{
    /**
     * @param hook overrides {@link TextComponentBase#getFormattedText()}.
     */
    void setFormattingHook(Supplier<String> hook);

    /**
     * @param hook overrides {@link TextComponentBase#getUnformattedText()}.
     */
    void setUnFormattedHook(Supplier<String> hook);

    ITextComponent copyNoSiblings();



}
