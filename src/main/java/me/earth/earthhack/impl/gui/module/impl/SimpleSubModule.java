package me.earth.earthhack.impl.gui.module.impl;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.module.SubModule;

public abstract class SimpleSubModule<T extends Module> extends Module
        implements SubModule<T>
{
    private final T parent;

    public SimpleSubModule(T parent, String name, Category category)
    {
        super(name, category);
        this.parent = parent;
    }

    @Override
    public T getParent()
    {
        return parent;
    }

}
