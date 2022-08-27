package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.api.module.data.DefaultData;

final class VelocityData extends DefaultData<Velocity>
{
    public VelocityData(Velocity velocity)
    {
        super(velocity);
        this.descriptions.put(module.knockBack,
            "Block the knockback you take from Hits.");
        this.descriptions.put(module.horizontal,
            "The factor of the horizontal knockback you receive.");
        this.descriptions.put(module.vertical,
            "The factor of the vertical knockback you receive.");
        this.descriptions.put(module.noPush,
            "Prevent getting pushed by other entities.");
        this.descriptions.put(module.explosions,
            "Block knockback received from explosions.");
        this.descriptions.put(module.bobbers,
            "Block fishing rod bobbers from moving you.");
        this.descriptions.put(module.water,
            "Prevent water from pushing you.");
        this.descriptions.put(module.blocks,
            "Prevent Blocks from pushing you out e.g. if you phased into one.");
        this.descriptions.put(module.shulkers,
            "Prevents Shulkers from pushing you.");
    }

    @Override
    public int getColor()
    {
        return 0xff3048FF;
    }

    @Override
    public String getDescription()
    {
        return "Stops knockback from various sources.";
    }

}
