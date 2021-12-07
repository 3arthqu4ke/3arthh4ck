package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.api.module.data.DefaultData;

final class EntityControlData extends DefaultData<EntityControl>
{
    public EntityControlData(EntityControl module)
    {
        super(module);
        register(module.control,
            "Controls entities that you normally can't control, like lamas.");
        register(module.jumpHeight, "Modify the JumpHeight of horses.");
        register(module.noAI, "Removes the AI of entities your ride.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Control entities you ride.";
    }

}
