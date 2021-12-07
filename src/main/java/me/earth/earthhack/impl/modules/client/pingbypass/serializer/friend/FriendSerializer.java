package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Invokes the friend command on the
 * PingBypass.
 */
public class FriendSerializer extends SubscriberImpl
        implements Serializer<PlayerEvent>, Globals
{
    private final Observer<PlayerEvent> observer = new ListenerFriends(this);
    private final Set<PlayerEvent> changed  = new LinkedHashSet<>();
    private boolean cleared;

    public FriendSerializer()
    {
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerDisconnect(this));
    }

    public void clear()
    {
        synchronized (changed)
        {
            changed.clear();
            Managers.FRIENDS.getPlayersWithUUID().forEach((k, v) ->
            {
                PlayerEvent event = new PlayerEvent(PlayerEventType.ADD, k, v);
                changed.add(event);
            });
            cleared = true;
        }
    }

    protected void onChange(PlayerEvent event)
    {
        if (!event.isCancelled())
        {
            synchronized (changed)
            {
                changed.add(event);
            }
        }
    }

    protected void onTick()
    {
        if (mc.player != null
                && mc.getConnection() != null
                && !changed.isEmpty())
        {
            if (cleared)
            {
                mc.getConnection().sendPacket(new CPacketChatMessage("@ServerFriend clear"));
                cleared = false;
            }

            PlayerEvent friend = pollFriend();
            if (friend != null)
            {
                serializeAndSend(friend);
            }
        }
    }

    @Override
    public void serializeAndSend(PlayerEvent event)
    {
        String command = "@ServerFriend";
        if (event.getType() == PlayerEventType.ADD)
        {
            command += " add " + event.getName() + " " + event.getUuid();
        }
        else
        {
            command += " del " + event.getName();
        }

        Earthhack.getLogger().info(command);
        CPacketChatMessage packet = new CPacketChatMessage(command);
        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
    }

    private PlayerEvent pollFriend()
    {
        if (!changed.isEmpty())
        {
            PlayerEvent friend = changed.iterator().next();
            changed.remove(friend);
            return friend;
        }

        return null;
    }

    public Observer<PlayerEvent> getObserver()
    {
        return observer;
    }

}
