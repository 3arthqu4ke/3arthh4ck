package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager extends SubscriberImpl implements Globals
{
    private final Map<EntityPlayer, PopCounter> pops =
            new ConcurrentHashMap<>();

    public CombatManager() {
        this.listeners.add(
            new EventListener<DeathEvent>(DeathEvent.class, Integer.MIN_VALUE) {
                @Override
                public void invoke(DeathEvent event) {
                    onDeath(event.getEntity());
                }
            });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketEntityStatus>>
                    (PacketEvent.Receive.class,
                            Integer.MIN_VALUE,
                            SPacketEntityStatus.class) {
                @Override
                public void invoke(
                        PacketEvent.Receive<SPacketEntityStatus> event) {
                    switch (event.getPacket().getOpCode()) {
                        case 3:
                            mc.addScheduledTask(() ->
                                onDeath(mc.world == null
                                    ? null
                                    : event.getPacket().getEntity(mc.world)));
                            break;
                        case 35:
                            mc.addScheduledTask(() ->
                                    onTotemPop(event.getPacket()));
                        default:
                    }
                }
            });
    }

    public void reset() {
        pops.clear();
    }

    public int getPops(Entity player) {
        if (player instanceof EntityPlayer) {
            PopCounter popCounter = pops.get(player);
            if (popCounter != null) {
                return popCounter.getPops();
            }
        }

        return 0;
    }

    public long lastPop(Entity player) {
        if (player instanceof EntityPlayer) {
            PopCounter popCounter = pops.get(player);
            if (popCounter != null) {
                return popCounter.lastPop();
            }
        }

        return Integer.MAX_VALUE;
    }

    private void onTotemPop(SPacketEntityStatus packet) {
        Entity player = packet.getEntity(mc.world);
        if (player instanceof EntityPlayer) {
            pops.computeIfAbsent((EntityPlayer) player, v -> new PopCounter())
                    .pop();
            TotemPopEvent totemPopEvent = new TotemPopEvent((EntityPlayer) player);
            Bus.EVENT_BUS.post(totemPopEvent);
        }
    }

    private void onDeath(Entity entity) {
        if (entity instanceof EntityPlayer) {
            pops.remove(entity);
        }
    }

    private static class PopCounter {
        private final StopWatch timer = new StopWatch();
        private int pops;

        public int getPops() {
            return pops;
        }

        public void pop() {
            timer.reset();
            pops++;
        }

        public void reset() {
            pops = 0;
        }

        public long lastPop() {
            return timer.getTime();
        }
    }

}
