package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.entity.living.player.IEntityPlayer;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;

public class PotionService extends SubscriberImpl implements Globals
{
    public PotionService()
    {
        this.listeners.add(new ReceiveListener<>(SPacketEntityStatus.class, e ->
        {
            if (e.getPacket().getOpCode() == 35)
            {
                mc.addScheduledTask(() -> onTotemPop(e.getPacket()));
            }
        }));

        this.listeners.add(new ReceiveListener<>(SPacketEntityMetadata.class,
            e -> mc.addScheduledTask(() -> onEntityMetaData(e.getPacket()))));
    }

    public void onTotemPop(SPacketEntityStatus packet)
    {
        if (mc.world == null)
        {
            return;
        }

        Entity entity = packet.getEntity(mc.world);
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase base = (EntityLivingBase) entity;
            base.clearActivePotions();
            base.addPotionEffect(
                    new PotionEffect(MobEffects.REGENERATION, 900, 1));
            base.addPotionEffect(
                    new PotionEffect(MobEffects.ABSORPTION, 100, 1));
        }
    }

    public void onEntityMetaData(SPacketEntityMetadata packet)
    {
        //noinspection ConstantConditions
        if (mc.world == null || packet.getDataManagerEntries() == null)
        {
            return;
        }

        Entity e = mc.world.getEntityByID(packet.getEntityId());
        if (e instanceof EntityPlayer)
        {
            EntityPlayer p = (EntityPlayer) e;
            for (EntityDataManager.DataEntry<?> entry :
                    packet.getDataManagerEntries())
            {
                if (entry.getKey().getId() == IEntityPlayer.getAbsorption()
                                                           .getId())
                {
                    float value = (Float) entry.getValue();
                    float prev  = p.getAbsorptionAmount();

                    if (value == 4.0f && prev < value) // Normal Golden Apple
                    {
                        p.addPotionEffect(
                            new PotionEffect(MobEffects.REGENERATION, 100, 1));
                        p.addPotionEffect(
                            new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
                    }
                    else if (value == 16.0f) // Enchanted Golden Apple
                    {
                        p.addPotionEffect(
                         new PotionEffect(MobEffects.REGENERATION, 400, 1));
                        p.addPotionEffect(
                         new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
                        p.addPotionEffect(
                         new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                        p.addPotionEffect(
                         new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
                    }

                    break;
                }
            }
        }
    }


}
