package me.earth.earthhack.impl.managers.client.event;

import me.earth.earthhack.api.event.events.Event;

import java.util.UUID;

public class PlayerEvent extends Event
{
    private final PlayerEventType type;
    private final String name;
    private final UUID uuid;

    public PlayerEvent(PlayerEventType type,
                       String name,
                       UUID uuid)
    {
        this.type   = type;
        this.name   = name;
        this.uuid   = uuid;
    }

    public PlayerEventType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

}
