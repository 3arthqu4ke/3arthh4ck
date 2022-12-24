package me.earth.earthhack.impl.util.minecraft.entity.module;

import me.earth.earthhack.api.module.data.DefaultData;

public class EntityTypeData<T extends EntityTypeModule> extends DefaultData<T>
{
    public EntityTypeData(T module)
    {
        super(module);
        register(module.players, "Targets Players.");
        register(module.monsters, "Targets Monsters.");
        register(module.animals, "Targets Animals.");
        register(module.tamedMobs, "Targets Tamed Mobs.");
        register(module.boss, "Targets Boss Monsters.");
        register(module.animals, "Targets Vehicles.");
        register(module.misc, "Targets Fireballs, ShulkerBullets etc.");
        register(module.misc, "Targets Unknown Entities.");
    }

}

