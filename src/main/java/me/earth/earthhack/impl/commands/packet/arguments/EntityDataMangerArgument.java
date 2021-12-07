package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityDataMangerArgument
        extends AbstractArgument<EntityDataManager> implements Globals
{
    public EntityDataMangerArgument()
    {
        super(EntityDataManager.class);
    }

    @Override
    public EntityDataManager fromString(String arg)
            throws ArgParseException
    {
        if (mc.world == null)
        {
            throw new ArgParseException("Minecraft.World was null!");
        }

        Entity entity = AbstractEntityArgument.getEntity(arg, Entity.class);
        if (entity == null)
        {
            throw new ArgParseException(
                    "Could not parse " + arg + " to Entity!");
        }

        return entity.getDataManager();
    }

}
