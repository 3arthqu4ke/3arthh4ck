package me.earth.earthhack.impl.modules.player.noinventorydesync;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;

import java.util.Objects;

public class ClickTimeStamp extends TimeStamp
{
    private final int windowId;
    private final short id;

    public ClickTimeStamp(int windowId, short id)
    {
        this.windowId = windowId;
        this.id = id;
    }

    public int getWindowId()
    {
        return windowId;
    }

    public short getId()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ClickTimeStamp)) return false;
        ClickTimeStamp that = (ClickTimeStamp) o;
        return getWindowId() == that.getWindowId() && getId() == that.getId();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getWindowId(), getId());
    }

}
