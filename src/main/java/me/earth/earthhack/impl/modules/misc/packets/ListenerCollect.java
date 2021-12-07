package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;

final class ListenerCollect extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketCollectItem>>
{
    private final Random rnd = new Random();

    public ListenerCollect(Packets module)
    {
        super(module, PacketEvent.Receive.class, SPacketCollectItem.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCollectItem> event)
    {
        if (!module.fastCollect.getValue())
        {
            return;
        }

        World world = mc.world;
        if (world == null)
        {
            return;
        }

        SPacketCollectItem p = event.getPacket();
        Entity entity = world.getEntityByID(p.getCollectedItemEntityID());
        Entity living = world.getEntityByID(p.getEntityID());
        if (entity == null
            || living != null && !(living instanceof EntityLivingBase))
        {
            return;
        }

        if (living == null)
        {
            living = mc.player;
            if (living == null)
            {
                return;
            }
        }

        event.setCancelled(true);
        SoundEvent soundEvent;
        float volume;
        float pitch;
        if (entity instanceof EntityXPOrb)
        {
            soundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
            volume = 0.1f;
            pitch = (rnd.nextFloat() - rnd.nextFloat()) * 0.35f + 0.9f;
        }
        else
        {
            soundEvent = SoundEvents.ENTITY_ITEM_PICKUP;
            volume = 0.2f;
            pitch = (rnd.nextFloat() - rnd.nextFloat()) * 1.4F + 2.0f;
        }

        if (entity instanceof EntityItem)
        {
            ((EntityItem) entity).getItem().setCount(p.getAmount());
        }

        Entity finalLiving = living;
        mc.addScheduledTask(() ->
        {
            world.playSound(entity.posX, entity.posY, entity.posZ,
                soundEvent, SoundCategory.PLAYERS, volume, pitch, false);
            mc.effectRenderer.addEffect(new ParticleItemPickup(
                    world, entity, finalLiving, 0.5f));
            mc.world.removeEntityFromWorld(p.getCollectedItemEntityID());
        });

        entity.setDead();
    }

}
