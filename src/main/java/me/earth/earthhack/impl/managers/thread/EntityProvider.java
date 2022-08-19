package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Makes snapshots of {@link WorldClient#loadedEntityList} and
 * {@link WorldClient#playerEntities} so you can access them
 * on another thread.
 */
@SuppressWarnings("unused")
public class EntityProvider extends SubscriberImpl implements Globals
{
    private volatile List<EntityPlayer> players;
    private volatile List<Entity> entities;

    public EntityProvider()
    {
        this.players  = Collections.emptyList();
        this.entities = Collections.emptyList();
        /* this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                update();
            }
        }); */
        this.listeners.add(new EventListener<TickEvent.PostWorldTick>(
                TickEvent.PostWorldTick.class)
        {
            @Override
            public void invoke(TickEvent.PostWorldTick event)
            {
                update();
            }
        });
    }

    private void update()
    {
        if (mc.world != null)
        {
            setLists(
                new ArrayList<>(mc.world.loadedEntityList),
                new ArrayList<>(mc.world.playerEntities));
        }
        else
        {
            setLists(Collections.emptyList(),
                    Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities,
                          List<EntityPlayer> playerEntities)
    {
        entities = loadedEntities;
        players  = playerEntities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     *  @return copy of {@link WorldClient#loadedEntityList}
     */
    public List<Entity> getEntities()
    {
        return entities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     * @return copy of {@link WorldClient#playerEntities}
     */
    public List<EntityPlayer> getPlayers()
    {
        return players;
    }

    public List<Entity> getEntitiesAsync()
    {
        return getEntities(!mc.isCallingFromMinecraftThread());
    }

    public List<EntityPlayer> getPlayersAsync()
    {
        return getPlayers(!mc.isCallingFromMinecraftThread());
    }

    public List<Entity> getEntities(boolean async)
    {
        return async ? entities : mc.world.loadedEntityList;
    }

    public List<EntityPlayer> getPlayers(boolean async)
    {
        return async ? players : mc.world.playerEntities;
    }

    public Entity getEntity(int id)
    {
        List<Entity> entities = getEntitiesAsync();
        if (entities != null)
        {
            return entities.stream()
                           .filter(e -> e != null && e.getEntityId() == id)
                           .findFirst()
                           .orElse(null);
        }

        return null;
    }

}
