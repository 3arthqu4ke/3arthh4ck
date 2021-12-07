package me.earth.earthhack.impl.core.ducks.entity;

import net.minecraft.util.DamageSource;

/**
 * Duck Interface for {@link net.minecraft.client.entity.EntityOtherPlayerMP}.
 */
public interface IEntityOtherPlayerMP
{
    default boolean attackEntitySuper(DamageSource source, float amount)
    {
        return true;
    }

    default boolean returnFromSuperAttack(DamageSource source, float amount)
    {
        return attackEntitySuper(source, amount);
    }

    default boolean shouldAttackSuper()
    {
        return false;
    }

}
