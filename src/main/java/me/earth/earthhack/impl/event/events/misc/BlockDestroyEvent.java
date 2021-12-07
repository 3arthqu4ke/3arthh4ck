package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import net.minecraft.util.math.BlockPos;

public class BlockDestroyEvent extends StageEvent
{
    private final BlockPos pos;
    private boolean used;

    public BlockDestroyEvent(Stage stage, BlockPos pos)
    {
        super(stage);
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public boolean isUsed()
    {
        return used;
    }

    public void setUsed(boolean used)
    {
        this.used = used;
    }

}
