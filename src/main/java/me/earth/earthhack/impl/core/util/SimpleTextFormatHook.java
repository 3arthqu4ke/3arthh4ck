package me.earth.earthhack.impl.core.util;

import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.function.Supplier;

/**
 * An implementation of {@link Supplier<String>} for
 * {@link ITextComponentBase},
 * that formats the given TextComponent with nothing
 * but the UnformattedComponentTexts of the component.
 */
public class SimpleTextFormatHook implements Supplier<String>
{
    private final TextComponentBase base;

    /**
     * @param base the base to format.
     */
    public SimpleTextFormatHook(TextComponentBase base)
    {
        this.base = base;
    }

    @Override
    public String get()
    {
        StringBuilder sb = new StringBuilder();

        for (ITextComponent component : base)
        {
            sb.append(component.getUnformattedComponentText());
        }

        return sb.toString();
    }

}
