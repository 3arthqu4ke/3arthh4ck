package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import me.earth.earthhack.impl.core.util.SimpleTextFormatHook;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.function.Supplier;

/**
 * An AbstractComponent that returns the
 * Text Supplied by the given {@link Supplier}.
 */
public class SuppliedComponent extends AbstractTextComponent
{
    /** The Supplier for this components text. */
    protected final Supplier<String> supplier;

    /**
     * Sets TextFormatting to a {@link SimpleTextFormatHook} with
     * {@link ITextComponentBase#setFormattingHook(Supplier)} and
     * {@link ITextComponentBase#setUnFormattedHook(Supplier)}.
     * If you don't want that, use those methods to set them to null.
     *
     * @param supplier the supplier supplying what is returned by
     *                 {@link SuppliedComponent#getText()}.
     */
    public SuppliedComponent(Supplier<String> supplier)
    {
        super(supplier.get());
        this.supplier = supplier;

        ((ITextComponentBase) this)
                .setFormattingHook(new SimpleTextFormatHook(this));
        ((ITextComponentBase) this)
                .setUnFormattedHook(new SimpleTextFormatHook(this));
    }

    @Override
    public String getText()
    {
        return supplier.get();
    }

    @Override
    public String getUnformattedComponentText()
    {
        return supplier.get();
    }

    @Override
    public TextComponentString createCopy()
    {
        SuppliedComponent copy = new SuppliedComponent(supplier);
        copy.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent component : this.getSiblings())
        {
            copy.appendSibling(component.createCopy());
        }

        return copy;
    }

}
