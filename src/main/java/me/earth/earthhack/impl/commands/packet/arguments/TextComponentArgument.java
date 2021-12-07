package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TextComponentArgument extends AbstractArgument<ITextComponent>
{
    public TextComponentArgument()
    {
        super(ITextComponent.class);
    }

    @Override
    public ITextComponent fromString(String argument) throws ArgParseException
    {
        return new TextComponentString(argument);
    }

}
