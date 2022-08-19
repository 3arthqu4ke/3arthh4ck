package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.play.server.SPacketSpawnObject;

import java.util.Comparator;

public class ListenerSpawn
        extends ModuleListener<Announcer, PacketEvent.Receive<SPacketSpawnObject>>
{

    public ListenerSpawn(Announcer module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if ((event.getPacket().getType() == 60
                || event.getPacket().getType() == 91)
                && (Math.abs(event.getPacket().getSpeedX() / 8000) > 0.001
                    || Math.abs(event.getPacket().getSpeedY() / 8000) > 0.001
                    || Math.abs(event.getPacket().getSpeedZ() / 8000) > 0.001)
                && module.miss.getValue())
        {
            Managers.ENTITIES.getPlayers()
                             .stream()
                             .filter(
                                 player -> player != mc.player && !Managers.FRIENDS.contains(
                                     player)).min(
                        Comparator.comparing(
                            player -> player.getDistanceSq(event.getPacket().getX(),
                                                           event.getPacket().getY(),
                                                           event.getPacket().getZ()))).ifPresent(
                        closestPlayer -> module.arrowMap.put(
                            event.getPacket().getEntityID(), closestPlayer));

        }
    }

}
