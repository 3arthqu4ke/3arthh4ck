package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.entity.EntityLivingBase}.
 */
public interface IEntityLivingBase
{
    /** @return the getArmSwingAnimationEnd field. */
    int armSwingAnimationEnd();

    /** @return the ticksSinceLastSwing field. */
    int getTicksSinceLastSwing();

    int getActiveItemStackUseCount();

    void setTicksSinceLastSwing(int ticks);

    void setActiveItemStackUseCount(int count);

    boolean getElytraFlag();

    void setLowestDura(float lowest);

    float getLowestDurability();

}
