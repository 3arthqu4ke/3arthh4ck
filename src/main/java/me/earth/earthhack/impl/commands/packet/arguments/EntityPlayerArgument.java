package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.util.DummyPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class EntityPlayerArgument extends
        AbstractEntityPlayerArgument<EntityPlayer>
{
    public EntityPlayerArgument()
    {
        super(EntityPlayer.class);
    }

    @Override
    protected EntityPlayer create()
    {
        return new DummyPlayer(mc.world);
    }

}
