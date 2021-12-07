package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.module.data.DefaultData;

final class EntitySpeedData extends DefaultData<EntitySpeed>
{
    public EntitySpeedData(EntitySpeed module)
    {
        super(module);
        register(module.speed, "The speed to move with.");
        register(module.noStuck,
                "Prevents you from getting stuck while riding.");
        register(module.resetStuck,
                "Makes you slower when using NoStuck.");
        register(module.stuckTime,
                "Time to be slowed down for after getting stuck.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Speed up the entity you are riding on.";
    }

}
