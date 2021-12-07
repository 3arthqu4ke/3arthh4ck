package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyEntity;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class WorldArgument extends AbstractArgument<World> implements Globals
{
    public WorldArgument()
    {
        super(World.class);
    }

    @Override
    public World fromString(String argument) throws ArgParseException
    {
        return mc.world;
    }

}
