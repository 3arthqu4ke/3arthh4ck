package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;

public class PreSlotClickEvent extends WindowClickEvent
{
    private final short id;

    public PreSlotClickEvent(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player, short id)
    {
        super(windowId, slotId, mouseButton, type, player);
        this.id = id;
    }

    public short getId()
    {
        return id;
    }

}
