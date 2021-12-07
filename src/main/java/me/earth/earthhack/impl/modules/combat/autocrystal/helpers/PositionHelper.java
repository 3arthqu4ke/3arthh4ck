package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;

import java.util.HashMap;
import java.util.Map;

public class PositionHelper extends SubscriberImpl implements Globals
{
    private final AutoCrystal module;
    private final Map<Entity, MotionTracker> motionTrackerMap;

    public PositionHelper(AutoCrystal module)
    {
        this.module = module;
        motionTrackerMap = new HashMap<>();
        this.listeners.add(new EventListener<WorldClientEvent>(WorldClientEvent.class)
        {
            @Override
            public void invoke(WorldClientEvent event)
            {
                motionTrackerMap.clear();
            }
        });
        this.listeners.add(new ReceiveListener<>(SPacketSpawnPlayer.class, event ->
        {
            event.addPostEvent(() ->
            {
                if (mc.world.getEntityByID(event.getPacket().getEntityID()) instanceof EntityPlayer)
                {
                    EntityPlayer player = (EntityPlayer) mc.world.getEntityByID(event.getPacket().getEntityID());
                    motionTrackerMap.put(player, new MotionTracker(mc.world, player));
                }
            });
        }));
        this.listeners.add(new EventListener<UpdateEntitiesEvent>(UpdateEntitiesEvent.class)
        {
            @Override
            public void invoke(UpdateEntitiesEvent event)
            {
                Map<Entity, MotionTracker> tempMap = new HashMap<>(motionTrackerMap);
                for (Map.Entry<Entity, MotionTracker> entry : tempMap.entrySet())
                {
                    if (EntityUtil.isDead(entry.getValue()))
                    {
                        motionTrackerMap.remove(entry.getValue());
                        continue;
                    }
                    entry.getValue().updateFromTrackedEntity();
                }
            }
        });
    }

    public MotionTracker getTrackerFromEntity(Entity player)
    {
        return motionTrackerMap.get(player);
    }

}
