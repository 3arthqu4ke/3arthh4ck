package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyBossInfo;
import net.minecraft.world.BossInfo;

public class BossInfoArgument extends AbstractArgument<BossInfo>
{
    public BossInfoArgument()
    {
        super(BossInfo.class);
    }

    @Override
    public BossInfo fromString(String argument) throws ArgParseException
    {
        return new DummyBossInfo();
    }

}
