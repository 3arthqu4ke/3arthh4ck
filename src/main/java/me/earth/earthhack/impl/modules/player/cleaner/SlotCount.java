package me.earth.earthhack.impl.modules.player.cleaner;

public class SlotCount
{
    private int count;
    private int slot;

    public SlotCount(int count, int slot)
    {
        this.count = count;
        this.slot = slot;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getSlot()
    {
        return slot;
    }

    public void setSlot(int slot)
    {
        this.slot = slot;
    }

}
