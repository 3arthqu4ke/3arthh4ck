package me.earth.earthhack.impl.util.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

//TODO: Rework to be faster
public enum EntityType
{
    Animal(EntityType::isAnimal),
    Player(EntityType::isPlayer),
    Boss(EntityType::isBoss),
    Monster(EntityType::isMonster),
    Vehicle(EntityType::isVehicle),
    Other(EntityType::isOther);

    /**
     * Contains Entity classes that don't
     * have a translation key mapped to a Name.
     */
    private static final Map<Class<? extends Entity>, String> entityNames;

    static
    {
        entityNames = new HashMap<>();
        entityNames.put(EntityItemFrame.class, "Item Frame");
        entityNames.put(EntityEnderCrystal.class, "End Crystal");
        entityNames.put(EntityMinecartEmpty.class, "Minecart");
        entityNames.put(EntityMinecart.class, "Minecart");
        entityNames.put(EntityMinecartFurnace.class, "Minecart with Furnace");
        entityNames.put(EntityMinecartTNT.class, "Minecart with TNT");
        //TODO: All Entities without translation keys.
    }

    final Predicate<Entity> predicate;

    EntityType(Predicate<Entity> predicate)
    {
        this.predicate = predicate;
    }

    public boolean is(Entity entity)
    {
        return predicate.test(entity);
    }

    public static boolean isPlayer(Entity entity)
    {
        return entity instanceof EntityPlayer;
    }

    public static boolean isAnimal(Entity entity)
    {
        return entity instanceof EntityPig
                || entity instanceof EntityParrot
                || entity instanceof EntityCow
                || entity instanceof EntitySheep
                || entity instanceof EntityChicken
                || entity instanceof EntitySquid
                || entity instanceof EntityBat
                || entity instanceof EntityVillager
                || entity instanceof EntityOcelot
                || entity instanceof EntityHorse
                || entity instanceof EntityLlama
                || entity instanceof EntityMule
                || entity instanceof EntityDonkey
                || entity instanceof EntitySkeletonHorse
                || entity instanceof EntityZombieHorse
                || entity instanceof EntitySnowman
                || entity instanceof EntityWolf
                || entity instanceof EntityRabbit && isFriendlyRabbit(entity);
    }

    public static boolean isMonster(Entity entity)
    {
        return entity instanceof EntityCreeper
                || entity instanceof EntityIllusionIllager
                || entity instanceof EntitySkeleton
                || entity instanceof EntityZombie
                || entity instanceof EntityBlaze
                || entity instanceof EntitySpider
                || entity instanceof EntityWitch
                || entity instanceof EntitySlime
                || entity instanceof EntitySilverfish
                || entity instanceof EntityGuardian
                || entity instanceof EntityEndermite
                || entity instanceof EntityGhast
                || entity instanceof EntityEvoker
                || entity instanceof EntityShulker
                || entity instanceof EntityWitherSkeleton
                || entity instanceof EntityStray
                || entity instanceof EntityVex
                || entity instanceof EntityVindicator
                || entity instanceof EntityPolarBear
                || entity instanceof EntityWolf
                || entity instanceof EntityEnderman
                || entity instanceof EntityRabbit
                || entity instanceof EntityIronGolem;
    }

    public static boolean isBoss(Entity entity)
    {
        return entity instanceof EntityDragon
                || entity instanceof EntityWither
                || entity instanceof EntityGiantZombie;
    }

    public static boolean isOther(Entity entity)
    {
        return entity instanceof EntityEnderCrystal
                || entity instanceof EntityEvokerFangs
                || entity instanceof EntityShulkerBullet
                || entity instanceof EntityFallingBlock
                || entity instanceof EntityFireball
                || entity instanceof EntityEnderEye
                || entity instanceof EntityEnderPearl;
    }

    public static boolean isVehicle(Entity entity)
    {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isAngry(Entity entity)
    {
        return entity instanceof EntityWolf && isAngryWolf(entity)
                || entity instanceof EntityPolarBear && isAngryPolarBear(entity)
                || entity instanceof EntityIronGolem && isAngryGolem(entity)
                || entity instanceof EntityEnderman && isAngryEnderMan(entity)
                || entity instanceof EntityPigZombie && isAngryPigMan(entity);
    }

    public static boolean isAngryEnderMan(Entity entity)
    {
        return entity instanceof EntityEnderman
                    && !((EntityEnderman)entity).isScreaming();
    }

    public static boolean isAngryPigMan(Entity entity)
    {
        return entity instanceof EntityPigZombie
                && entity.rotationPitch == 0.0F
                && ((EntityPigZombie) entity).getRevengeTimer() <= 0;
    }

    public static boolean isAngryGolem(Entity entity)
    {
        return entity instanceof EntityIronGolem
                    && entity.rotationPitch == 0.0F;
    }

    public static boolean isAngryWolf(Entity entity)
    {
        return entity instanceof EntityWolf && !((EntityWolf)entity).isAngry();
    }

    public static boolean isAngryPolarBear(Entity entity)
    {
        return entity instanceof EntityPolarBear
                && entity.rotationPitch == 0.0F
                && ((EntityPolarBear) entity).getRevengeTimer() <= 0;
    }

    public static boolean isFriendlyRabbit(Entity entity)
    {
        return entity instanceof EntityRabbit
                    && ((EntityRabbit)entity).getRabbitType() != 99;
    }

    public static String getName(Entity entity)
    {
        String name = entityNames.get(entity.getClass());
        if (name != null)
        {
            return name;
        }

        return entity.getName();
    }

}
