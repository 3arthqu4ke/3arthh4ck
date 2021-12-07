package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.util.math.Passable;
import net.minecraft.entity.item.EntityEnderCrystal;

public interface InstantAttackingModule extends AttackingModule
{
    @SuppressWarnings("unused")
    default void postAttack(EntityEnderCrystal entity)
    {
        // Reserved for actions that can happen
        // after the crystal has been attacked.
    }

    boolean shouldAttack(EntityEnderCrystal entity);

    Passable getTimer();

    int getBreakDelay();

    int getCooldown();

}
