package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyPublicKey;

import java.security.PublicKey;

public class PublicKeyArgument extends AbstractArgument<PublicKey>
{
    public PublicKeyArgument()
    {
        super(PublicKey.class);
    }

    @Override
    public PublicKey fromString(String argument) throws ArgParseException
    {
        return new DummyPublicKey();
    }

}
