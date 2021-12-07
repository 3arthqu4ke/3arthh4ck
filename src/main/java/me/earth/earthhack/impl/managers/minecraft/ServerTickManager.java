package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Timer;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author megyn
 * fixed bugs with old version, now accurate within ~5 ms if start of tick is counted as when the time update packet is sent
 * TODO: use average time between packets being sent to more accurately approximate TPS, this will increase accuracy
 */
public class ServerTickManager extends SubscriberImpl implements Globals
{

    private int serverTicks;
    private Map<BlockPos, Long> timeMap = new HashMap<>();
    private final Timer serverTickTimer = new Timer();
    private boolean flag = true;
    private boolean initialized = false; // will be used for checks in the future

    private final ArrayDeque<Integer> spawnObjectTimes = new ArrayDeque<>();
    private int averageSpawnObjectTime; // around 8-9 in vanilla

    public ServerTickManager()
    {

        this.listeners.add(
                new EventListener<WorldClientEvent>
                        (WorldClientEvent.class)
        {
            @Override
            public void invoke(WorldClientEvent event)
            {
                if (event.getClient().isRemote)
                {
                    reset();
                }
            }
        });

        this.listeners.add(
                new EventListener<PacketEvent.Receive<SPacketSpawnObject>>
                        (PacketEvent.Receive.class,
                                Integer.MAX_VALUE,
                                SPacketSpawnObject.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
            {
                if (mc.world != null
                        && mc.world.isRemote) {
                    onSpawnObject();
                }
                /*BlockPos pos = new BlockPos(event.getPacket().getX(), event.getPacket().getY(), event.getPacket().getZ());
                Long id = timeMap.get(pos);
                System.out.println("BlockPos: " + pos);
                if (id != null) {
                    System.out.println("Crystal spawned! Time since placement: " + (System.currentTimeMillis() - id));
                    System.out.println("Sent at: " + getTickTimeAdjustedForServerPackets());
                }*/
            }
        });

        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketTimeUpdate>>
                (PacketEvent.Receive.class,
                        Integer.MAX_VALUE,
                        SPacketTimeUpdate.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketTimeUpdate> event)
            {
                if (mc.world != null
                        && mc.world.isRemote)
                {
                    reset();
                }
            }
        });

        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class) {
            @Override
            public void invoke(DisconnectEvent event)
            {
                initialized = false;
            }
        });

        /*this.listeners.add(new EventListener<PacketEvent.Send<CPacketPlayerTryUseItemOnBlock>>(PacketEvent.Send.class, Integer.MAX_VALUE, CPacketPlayerTryUseItemOnBlock.class) {
            @Override
            public void invoke(PacketEvent.Send<CPacketPlayerTryUseItemOnBlock> event) {
                if (InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                    System.out.println("PlacePos: " + event.getPacket().getPos().up());
                    System.out.println("Server will receive at: " + getTickTimeAdjusted());
                    timeMap.put(event.getPacket().getPos().up(), System.currentTimeMillis());
                }
            }
        });*/

        /*this.listeners.add(
                new EventListener<GameLoopEvent>
                        (GameLoopEvent.class)
        {
            @Override
            public void invoke(GameLoopEvent event)
            {
                if (flag && (serverTickTimer.getTime() / 50) >= 20) {
                    Earthhack.getLogger().info("Server tick starting! Current time: " + System.currentTimeMillis());
                    Earthhack.getLogger().info("Current time into tick: " + getTickTime());
                    flag = false;
                }
            }
        }); For debugging. */

        /*this.listeners.add(new EventListener<PacketEvent.Receive<SPacketSpawnObject>>
                (PacketEvent.Receive.class,
                        Integer.MAX_VALUE,
                        SPacketSpawnObject.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
            {
                System.out.println("Object spawned! Time into current tick: " + getTickTime());
                System.out.println("Sent at: " + getTickTimeAdjustedForServerPackets());
            }
        }); For debugging. */

    }

    /**
     * Retrieves the time into the current server tick
     * @return time into the current server tick
     */
    public int getTickTime() {
        if (serverTickTimer.getTime() < 50) return (int) serverTickTimer.getTime();
        return (int) (serverTickTimer.getTime() % getServerTickLengthMS());
    }

    /**
     * Retrieves the time into a tick that the server will receive a sent packet (experimental)
     * @return time that sent packets will be received by the client
     */
    public int getTickTimeAdjusted() {
        int time = getTickTime() + (ServerUtil.getPingNoPingSpoof() / 2);
        if (time < getServerTickLengthMS()) return time; // redundant? idrk how modulus works in java
        return time % getServerTickLengthMS();
    }

    /**
     * Get the time into a tick that a packet was sent by the server
     * @return tick time adjusted for server packets
     */
    public int getTickTimeAdjustedForServerPackets() {
        int time = getTickTime() - (ServerUtil.getPingNoPingSpoof() / 2);
        if (time < getServerTickLengthMS() && time > 0) return time; // redundant? idrk how modulus works in java
        if (time < 0) return time + getServerTickLengthMS();
        return time % getServerTickLengthMS();
    }

    public void reset() {
        serverTickTimer.reset();
        serverTickTimer.adjust(ServerUtil.getPingNoPingSpoof() / 2);
        // flag = true;
        initialized = true;
    }

    public int getServerTickLengthMS() {
        if (Managers.TPS.getTps() == 0) return 50;
        return (int) (50 * (20.0f / Managers.TPS.getTps()));
    }

    public void onSpawnObject() {
        int time = getTickTimeAdjustedForServerPackets();
        if (spawnObjectTimes.size() > 10) spawnObjectTimes.poll();
        spawnObjectTimes.add(time);
        int totalTime = 0;
        for (int spawnTime : spawnObjectTimes) {
            totalTime += spawnTime;
        }
        averageSpawnObjectTime = totalTime / spawnObjectTimes.size();
    }

    public int normalize(int toNormalize) {
        while (toNormalize < 0) {
            toNormalize += getServerTickLengthMS();
        }
        while (toNormalize > getServerTickLengthMS()) {
            toNormalize -= getServerTickLengthMS();
        }
        return toNormalize;
    }

    public boolean valid(int currentTime, int minTime, int maxTime) {
        if (minTime > maxTime) {
            return currentTime >= minTime || currentTime <= maxTime;
        } else {
            return currentTime >= minTime && currentTime <= maxTime;
        }
    }

    public int getSpawnTime() {
        return averageSpawnObjectTime;
    }

}