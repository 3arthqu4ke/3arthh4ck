package me.earth.earthhack.pingbypass.proxy;

import me.earth.earthhack.pingbypass.protocol.s2c.S2CWorldTickPacket;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.world.World;

import java.util.Objects;

// TODO: Passengers, Leashes???
public class EntitySender {
    public static void sendEntities(World world, NetworkManager manager) {
        for (Entity entity : world.loadedEntityList) {
            if (!(entity instanceof EntityPlayerSP)) {
                Packet<?> spawnPacket = createSpawnPacket(entity);
                manager.sendPacket(spawnPacket);
            }
        }

        for (Entity weatherEffect : world.weatherEffects) {
            manager.sendPacket(new SPacketSpawnGlobalEntity(weatherEffect));
        }

        manager.sendPacket(new S2CWorldTickPacket());

        for (Entity entity : world.loadedEntityList) {
            if (!entity.getPassengers().isEmpty()) {
                manager.sendPacket(new SPacketSetPassengers(entity));
            }

            if (entity instanceof EntityLivingBase) {
                for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
                    ItemStack itemstack = ((EntityLivingBase) entity).getItemStackFromSlot(
                        entityequipmentslot);

                    if (!itemstack.isEmpty()) {
                        manager.sendPacket(
                            new SPacketEntityEquipment(entity.getEntityId(),
                                                       entityequipmentslot,
                                                       itemstack));
                    }
                }
            }

            if (entity instanceof EntityLivingBase) {
                manager.sendPacket(new SPacketEntityProperties(
                    entity.getEntityId(),
                    ((EntityLivingBase) entity).getAttributeMap().getAllAttributes()));
            }
        }
    }

    // TF mojang
    public static Packet<?> createSpawnPacket(Entity entity)
    {
        if (entity instanceof EntityOtherPlayerMP)
        {
            return new SPacketSpawnPlayer((EntityPlayer) entity);
        }
        else if (entity instanceof IAnimals)
        {
            return new SPacketSpawnMob((EntityLivingBase)entity);
        }
        else if (entity instanceof EntityPainting)
        {
            return new SPacketSpawnPainting((EntityPainting)entity);
        }
        else if (entity instanceof EntityItem)
        {
            return new SPacketSpawnObject(entity, 2, 1);
        }
        else if (entity instanceof EntityMinecart)
        {
            EntityMinecart entityminecart = (EntityMinecart)entity;
            return new SPacketSpawnObject(entity, 10, entityminecart.getType().getId());
        }
        else if (entity instanceof EntityBoat)
        {
            return new SPacketSpawnObject(entity, 1);
        }
        else if (entity instanceof EntityXPOrb)
        {
            return new SPacketSpawnExperienceOrb((EntityXPOrb)entity);
        }
        else if (entity instanceof EntityFishHook)
        {
            Entity entity2 = ((EntityFishHook)entity).getAngler();
            //noinspection ConstantConditions
            return new SPacketSpawnObject(entity, 90, entity2 == null ? entity.getEntityId() : entity2.getEntityId());
        }
        else if (entity instanceof EntitySpectralArrow)
        {
            Entity entity1 = ((EntitySpectralArrow)entity).shootingEntity;
            return new SPacketSpawnObject(entity, 91, 1 + (entity1 == null ? entity.getEntityId() : entity1.getEntityId()));
        }
        else if (entity instanceof EntityTippedArrow)
        {
            Entity shootingEntity = ((EntityArrow) entity).shootingEntity;
            //noinspection ConstantConditions
            return new SPacketSpawnObject(shootingEntity, 60, 1 + (shootingEntity == null ? entity.getEntityId() : shootingEntity.getEntityId()));
        }
        else if (entity instanceof EntitySnowball)
        {
            return new SPacketSpawnObject(entity, 61);
        }
        else if (entity instanceof EntityLlamaSpit)
        {
            return new SPacketSpawnObject(entity, 68);
        }
        else if (entity instanceof EntityPotion)
        {
            return new SPacketSpawnObject(entity, 73);
        }
        else if (entity instanceof EntityExpBottle)
        {
            return new SPacketSpawnObject(entity, 75);
        }
        else if (entity instanceof EntityEnderPearl)
        {
            return new SPacketSpawnObject(entity, 65);
        }
        else if (entity instanceof EntityEnderEye)
        {
            return new SPacketSpawnObject(entity, 72);
        }
        else if (entity instanceof EntityFireworkRocket)
        {
            return new SPacketSpawnObject(entity, 76);
        }
        else if (entity instanceof EntityFireball)
        {
            EntityFireball entityfireball = (EntityFireball)entity;
            SPacketSpawnObject spacketspawnobject;
            int i = 63;

            if (entity instanceof EntitySmallFireball)
            {
                i = 64;
            }
            else if (entity instanceof EntityDragonFireball)
            {
                i = 93;
            }
            else if (entity instanceof EntityWitherSkull)
            {
                i = 66;
            }

            if (entityfireball.shootingEntity != null)
            {
                spacketspawnobject = new SPacketSpawnObject(entity, i, ((EntityFireball)entity).shootingEntity.getEntityId());
            }
            else
            {
                spacketspawnobject = new SPacketSpawnObject(entity, i, 0);
            }

            spacketspawnobject.setSpeedX((int)(entityfireball.accelerationX * 8000.0D));
            spacketspawnobject.setSpeedY((int)(entityfireball.accelerationY * 8000.0D));
            spacketspawnobject.setSpeedZ((int)(entityfireball.accelerationZ * 8000.0D));
            return spacketspawnobject;
        }
        else if (entity instanceof EntityShulkerBullet)
        {
            SPacketSpawnObject shulkerPacket = new SPacketSpawnObject(entity, 67, 0);
            shulkerPacket.setSpeedX((int)(entity.motionX * 8000.0D));
            shulkerPacket.setSpeedY((int)(entity.motionY * 8000.0D));
            shulkerPacket.setSpeedZ((int)(entity.motionZ * 8000.0D));
            return shulkerPacket;
        }
        else if (entity instanceof EntityEgg)
        {
            return new SPacketSpawnObject(entity, 62);
        }
        else if (entity instanceof EntityEvokerFangs)
        {
            return new SPacketSpawnObject(entity, 79);
        }
        else if (entity instanceof EntityTNTPrimed)
        {
            return new SPacketSpawnObject(entity, 50);
        }
        else if (entity instanceof EntityEnderCrystal)
        {
            return new SPacketSpawnObject(entity, 51);
        }
        else if (entity instanceof EntityFallingBlock)
        {
            EntityFallingBlock entityfallingblock = (EntityFallingBlock)entity;
            return new SPacketSpawnObject(entity, 70, Block.getStateId(
                Objects.requireNonNull(entityfallingblock.getBlock())));
        }
        else if (entity instanceof EntityArmorStand)
        {
            return new SPacketSpawnObject(entity, 78);
        }
        else if (entity instanceof EntityItemFrame)
        {
            EntityItemFrame entityitemframe = (EntityItemFrame)entity;
            assert entityitemframe.facingDirection != null;
            return new SPacketSpawnObject(entity, 71, entityitemframe.facingDirection.getHorizontalIndex(), entityitemframe.getHangingPosition());
        }
        else if (entity instanceof EntityLeashKnot)
        {
            EntityLeashKnot entityleashknot = (EntityLeashKnot)entity;
            return new SPacketSpawnObject(entity, 77, 0, entityleashknot.getHangingPosition());
        }
        else if (entity instanceof EntityAreaEffectCloud)
        {
            return new SPacketSpawnObject(entity, 3);
        }
        else
        {
            throw new IllegalArgumentException("Don't know how to add " + entity.getClass() + "!");
        }
    }

}
