package me.earth.earthhack.impl.modules.movement.safewalk;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;

public class SafeWalk extends Module
{
    public SafeWalk()
    {
        super("SafeWalk", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.setData(new SimpleData(this, "Never fall down edges."));
    }

}
