package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.core.ducks.util.IHoverable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SuppliedHoverableComponent extends SuppliedComponent
        implements IHoverable
{
    private final BooleanSupplier canBeHovered;

    public SuppliedHoverableComponent(Supplier<String> supplier,
                                      BooleanSupplier canBeHovered)
    {
        super(supplier);
        this.canBeHovered = canBeHovered;
    }

    @Override
    public boolean canBeHovered()
    {
        return canBeHovered.getAsBoolean();
    }

    @Override
    public TextComponentString createCopy()
    {
        SuppliedHoverableComponent copy =
                new SuppliedHoverableComponent(supplier, canBeHovered);

        copy.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent component : this.getSiblings())
        {
            copy.appendSibling(component.createCopy());
        }

        return copy;
    }

}
