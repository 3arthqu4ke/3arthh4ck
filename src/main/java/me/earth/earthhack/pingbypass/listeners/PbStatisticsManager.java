package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.stats.StatBase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PbStatisticsManager extends EventListener<PacketEvent.Receive<SPacketStatistics>> {
    private Map<StatBase, Integer> map = Collections.emptyMap();

    public PbStatisticsManager() {
        super(PacketEvent.Receive.class, SPacketStatistics.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketStatistics> event) {
        //noinspection ConstantConditions
        if (event.getPacket().getStatisticMap() != null) {
            this.map = new HashMap<>(event.getPacket().getStatisticMap());
        }
    }

    public Map<StatBase, Integer> getMap() {
        return map;
    }

}
