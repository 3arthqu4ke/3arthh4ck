package me.earth.earthhack.impl.util.helpers.blocks.attack;

import net.minecraft.entity.Entity;

import java.util.List;

public interface AttackHelper
{
    boolean attackAny(List<Entity> entities, AttackingModule module);

}
