package me.earth.earthhack.impl.modules.misc.mobowner;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

import java.util.UUID;

final class ListenerTick extends ModuleListener<MobOwner, TickEvent>
{
    public ListenerTick(MobOwner module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.world != null)
        {
            for (Entity entity : mc.world.getLoadedEntityList())
            {
                if (entity != null && !entity.getAlwaysRenderNameTag())
                {
                    if (entity instanceof EntityTameable)
                    {
                        EntityTameable tameable = (EntityTameable) entity;
                        if (tameable.isTamed())
                        {
                            renderNametag(entity, tameable.getOwnerId());
                        }
                    }
                    else if (entity instanceof AbstractHorse)
                    {
                        AbstractHorse horse = (AbstractHorse) entity;
                        if (horse.isTame())
                        {
                            renderNametag(entity, horse.getOwnerUniqueId());
                        }
                    }
                }
            }
        }
    }

    private void renderNametag(Entity entity, UUID id)
    {
        if (id != null)
        {
            if (module.cache.containsKey(id))
            {
                String owner = module.cache.get(id);
                if (owner != null)
                {
                    entity.setAlwaysRenderNameTag(true);
                    entity.setCustomNameTag(owner);
                }
            }
            else
            {
                module.cache.put(id, null);
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, id)
                {
                    @Override
                    public void onSuccess()
                    {
                        mc.addScheduledTask(() -> module.cache.put(id, name));
                    }

                    @Override
                    public void onFailure()
                    {
                        mc.addScheduledTask(() -> module.cache.put(id, null));
                    }
                });
            }
        }
    }

}
