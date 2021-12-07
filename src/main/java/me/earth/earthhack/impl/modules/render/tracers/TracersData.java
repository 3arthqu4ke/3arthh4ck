package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.api.module.data.DefaultData;

final class TracersData extends DefaultData<Tracers>
{
    public TracersData(Tracers module)
    {
        super(module);
        register(module.mode, "-Outline draws an outline around entities." +
                "\n-Fill draws a filled box around entities." +
                "\n-Stem draws a line at the entity." +
                "\n-Off just a tracer.");
        register(module.target, "The part of the entities hitbox to trace to.");
        register(module.players, "Draw tracers to players.");
        register(module.friends, "Draw tracers to friends.");
        register(module.invisibles, "Draw tracers to invisible entities.");
        register(module.monsters, "Draw tracers to monsters.");
        register(module.animals, "Draw tracers to animals.");
        register(module.vehicles, "Draw tracers to vehicles.");
        register(module.items, "Draw tracers to items.");
        register(module.misc, "Draw tracers to other entities.");
        register(module.lines, "Turns the actual tracers on and off.");
        register(module.lineWidth, "The width of a tracer.");
        register(module.tracers, "The maximum amount of tracers to draw.");
        register(module.minRange, "Entities have to be at least this" +
                " far away to get a tracer drawn to them.");
        register(module.maxRange, "Entities need to be within this range" +
                " to get a tracer drawn to them.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Draws lines to Entities.";
    }

}
