package me.earth.earthhack.impl.gui.chat.clickevents;

import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import net.minecraft.util.text.event.ClickEvent;

import java.util.function.Supplier;

public class SuppliedRunnableClickEvent extends ClickEvent
        implements IClickEvent
{
    private final Supplier<Runnable> supplier;

    public SuppliedRunnableClickEvent(Supplier<Runnable> supplier)
    {
        super(Action.RUN_COMMAND, "$runnable-supplied$");
        this.supplier = supplier;
    }

    @Override
    public void setRunnable(Runnable runnable) { }

    @Override
    public Runnable getRunnable()
    {
        return supplier.get();
    }

}
