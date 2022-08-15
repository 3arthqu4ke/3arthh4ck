package me.earth.earthhack.impl.managers.client.event;

import me.earth.earthhack.api.event.events.Event;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerEvent)) return false;
        PlayerEvent that = (PlayerEvent) o;
        return getType() == that.getType() && Objects.equals(getName(),
                                                             that.getName()) && Objects.equals(
            getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getName(), getUuid());
    }

}
