package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractEntityPlayerArgument<T extends EntityPlayer>
        extends AbstractEntityArgument<T>
{
    public AbstractEntityPlayerArgument(Class<T> type)
    {
        super(type);
    }

    protected abstract T create();

    @Override
    public T fromString(String arg) throws ArgParseException
    {
        if (mc.world == null)
        {
            throw new ArgParseException("Minecraft.world was null!");
        }

        T entity = AbstractEntityArgument.getEntity(arg, this.directType);
        if (entity == null)
        {
            int id = -1337;
            try
            {
                id = (int) Long.parseLong(arg);
            }
            catch (Exception ignored) { }
            entity = create();
            entity.setEntityId(id);
        }

        return entity;
    }

}
