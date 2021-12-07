package me.earth.earthhack.impl.gui.module;

import me.earth.earthhack.api.module.Module;

/**
 * SubModules belong to a parent module.
 * If the parent module is null they are
 * handled just like normal modules, otherwise
 * they will be rendered differently in the gui.
 */
public interface SubModule<T extends Module>
{
    /**
     * @return the parent module. Might be <tt>null</tt>.
     */
    T getParent();

}
