package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent {
    private final EntityPlayer entity;

    public TotemPopEvent(EntityPlayer entity)
    {
        this.entity = entity;
    }

    public EntityPlayer getEntity()
    {
        return entity;
    }

}