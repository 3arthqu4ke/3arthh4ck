package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SClearFriendsPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SFriendPacket;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger(FriendSerializer.class);

    private final Observer<PlayerEvent> observer = new ListenerFriends(this);
    private final Set<PlayerEvent> changed  = new LinkedHashSet<>();
    private final PingBypassModule module;
    private boolean cleared;

    public FriendSerializer(PingBypassModule module)
    {
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.module = module;
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
        if (mc.getConnection() != null)
        {
            if (cleared)
            {
                LOGGER.info("Clearing friends...");
                if (module.isOld()) {
                    mc.getConnection().sendPacket(new CPacketChatMessage("@ServerFriend clear"));
                } else {
                    mc.getConnection().sendPacket(new C2SClearFriendsPacket());
                }

                cleared = false;
            }

            if (!changed.isEmpty())
            {
                PlayerEvent friend;
                int i = 0;
                while ((friend = pollFriend()) != null && i++ < 500)
                {
                    serializeAndSend(friend);
                }
            }
        }
    }

    @Override
    public void serializeAndSend(PlayerEvent event)
    {
        if (!module.isOld())
        {
            LOGGER.info("Sending C2SFriendPacket " + event.getName());
            Objects.requireNonNull(mc.getConnection()).sendPacket(
                new C2SFriendPacket(event));
            return;
        }

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
        synchronized (changed)
        {
            if (!changed.isEmpty())
            {
                PlayerEvent friend = changed.iterator().next();
                changed.remove(friend);
                return friend;
            }
        }

        return null;
    }

    public Observer<PlayerEvent> getObserver()
    {
        return observer;
    }

}
