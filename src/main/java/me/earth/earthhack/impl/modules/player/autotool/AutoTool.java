package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;

public class AutoTool extends Module
{
    protected int lastSlot = -1;
    protected boolean set  = false;

    public AutoTool()
    {
        super("AutoTool", Category.Player);
        this.listeners.add(new ListenerDamageBlock(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.setData(new SimpleData(this,
                "Automatically selects the best Tool when mining a block."));
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    public void reset()
    {
        lastSlot = -1;
        set      = false;
    }

}
