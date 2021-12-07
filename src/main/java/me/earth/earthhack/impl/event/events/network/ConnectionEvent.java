package me.earth.earthhack.impl.event.events.network;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class ConnectionEvent
{
    private final EntityPlayer player;
    private final String name;
    private final UUID uuid;

    private ConnectionEvent(String name, UUID uuid, EntityPlayer player)
    {
        this.player = player;
        this.name   = name;
        this.uuid   = uuid;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public String getName()
    {
        if (name == null && player != null)
        {
            return player.getName();
        }

        return name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public static class Join extends ConnectionEvent
    {
        public Join(String name, UUID uuid, EntityPlayer player)
        {
            super(name, uuid, player);
        }
    }

    public static class Leave extends ConnectionEvent
    {
        public Leave(String name, UUID uuid, EntityPlayer player)
        {
            super(name, uuid, player);
        }
    }

}
