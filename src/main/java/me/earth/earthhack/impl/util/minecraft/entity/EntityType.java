package me.earth.earthhack.impl.util.minecraft.entity;

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

import java.awt.*;
import java.util.function.Supplier;

public enum EntityType
{
    Animal(new Color(0, 200, 0, 255)),
    Monster(new Color(200, 60, 60, 255)),
    Player(new Color(255, 255, 255, 255)),
    Boss(new Color(40, 0, 255, 255)),
    Vehicle(new Color(200, 100, 0, 255)),
    Other(new Color(200, 100, 200, 255)),
    Entity(new Color(255, 255, 0, 255));

    private final Color color;

    EntityType(Color color)
    {
        this.color = color;
    }

    /** @return the Color belonging to the given EntityType. */
    public Color getColor()
    {
        return color;
    }

    /*------------------- Static Util -------------------*/
    public static Supplier<EntityType> getEntityType(Entity entity)
    {
        if (entity instanceof EntityWolf)
        {
            return () -> isAngryWolf((EntityWolf) entity)
                            ? Monster
                            : Animal;
        }

        if (entity instanceof EntityEnderman)
        {
            return () -> isAngryEnderMan((EntityEnderman) entity)
                                ? Monster
                                : Entity;
        }

        if (entity instanceof EntityPolarBear)
        {
            return () -> isAngryPolarBear((EntityPolarBear) entity)
                                ? Monster
                                : Animal;
        }

        if (entity instanceof EntityPigZombie)
        {
            return () -> entity.rotationPitch == 0.0F
                            && ((EntityPigZombie) entity).getRevengeTimer() <= 0
                                ? Monster
                                : Entity;
        }

        if (entity instanceof EntityIronGolem)
        {
            return () -> isAngryGolem((EntityIronGolem) entity)
                                ? Monster
                                : Entity;
        }

        if (entity instanceof EntityVillager)
        {
            return () -> Entity;
        }

        if (entity instanceof EntityRabbit)
        {
            return () -> isFriendlyRabbit((EntityRabbit) entity)
                            ? Animal
                            : Monster;
        }

        if (isAnimal(entity))
        {
            return () -> Animal;
        }

        if (isMonster(entity))
        {
            return () -> Monster;
        }

        if (isPlayer(entity))
        {
            return () -> Player;
        }

        if (isVehicle(entity))
        {
            return () -> Vehicle;
        }

        if (isBoss(entity))
        {
            return () -> Boss;
        }

        if (isOther(entity))
        {
            return () -> Other;
        }

        return () -> Entity;
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
                || entity instanceof EntityRabbit
                    && isFriendlyRabbit((EntityRabbit) entity);
    }

    public static boolean isMonster(Entity entity)
    {
        return entity instanceof EntityCreeper
                || entity instanceof EntityIllusionIllager
                || entity instanceof EntitySkeleton
                || entity instanceof EntityZombie
                    && !(entity instanceof EntityPigZombie)
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
                    && !isAngryPolarBear((EntityPolarBear) entity)
                || entity instanceof EntityWolf
                    && !isAngryWolf((EntityWolf) entity)
                || entity instanceof EntityPigZombie
                    && !isAngryPigMan(entity)
                || entity instanceof EntityEnderman
                    && !isAngryEnderMan((EntityEnderman) entity)
                || entity instanceof EntityRabbit
                    && !isFriendlyRabbit((EntityRabbit) entity)
                || entity instanceof EntityIronGolem
                    && !isAngryGolem((EntityIronGolem) entity);
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

    public static boolean isAngryEnderMan(EntityEnderman enderman)
    {
        return enderman.isScreaming();
    }

    public static boolean isAngryPigMan(Entity entity)
    {
        return entity instanceof EntityPigZombie
                && entity.rotationPitch == 0.0F
                && ((EntityPigZombie) entity).getRevengeTimer() <= 0;
    }

    public static boolean isAngryGolem(EntityIronGolem ironGolem)
    {
        return ironGolem.rotationPitch == 0.0F;
    }

    public static boolean isAngryWolf(EntityWolf wolf)
    {
        return wolf.isAngry();
    }

    public static boolean isAngryPolarBear(EntityPolarBear polarBear)
    {
        return polarBear.rotationPitch == 0.0f
                && polarBear.getRevengeTimer() <= 0;
    }

    public static boolean isFriendlyRabbit(EntityRabbit rabbit)
    {
        return rabbit.getRabbitType() != 99;
    }

}
