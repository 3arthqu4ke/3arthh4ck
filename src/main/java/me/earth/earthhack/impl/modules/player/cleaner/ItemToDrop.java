package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.setting.Setting;

public class ItemToDrop extends SlotCount
{
    private final Setting<Integer> setting;
    private int stacks;

    public ItemToDrop(Setting<Integer> setting)
    {
        super(Integer.MAX_VALUE, 0);
        this.setting = setting;
    }

    public void addSlot(int slot, int count)
    {
        stacks++;
        if (count < getCount())
        {
            setSlot(slot);
            setCount(count);
        }
    }

    public boolean shouldDrop()
    {
        return setting == null || setting.getValue() < stacks;
    }

}
