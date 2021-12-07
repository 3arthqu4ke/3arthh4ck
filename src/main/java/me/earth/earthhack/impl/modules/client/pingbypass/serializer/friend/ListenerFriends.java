package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;

final class ListenerFriends implements Observer<PlayerEvent>
{
    private final FriendSerializer serializer;

    public ListenerFriends(FriendSerializer serializer)
    {
        this.serializer = serializer;
    }

    @Override
    public void onChange(PlayerEvent event)
    {
        serializer.onChange(event);
    }

}
