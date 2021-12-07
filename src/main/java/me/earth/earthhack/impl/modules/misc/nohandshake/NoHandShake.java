package me.earth.earthhack.impl.modules.misc.nohandshake;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;

public class NoHandShake extends Module
{
    public NoHandShake()
    {
        super("NoHandShake", Category.Misc);
        this.listeners.add(new ListenerCustomPayload(this));
    }

}
