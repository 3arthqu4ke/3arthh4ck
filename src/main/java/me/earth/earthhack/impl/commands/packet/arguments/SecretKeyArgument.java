package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummySecretKey;

import javax.crypto.SecretKey;

public class SecretKeyArgument extends AbstractArgument<SecretKey>
{
    public SecretKeyArgument()
    {
        super(SecretKey.class);
    }

    @Override
    public SecretKey fromString(String argument) throws ArgParseException
    {
        return new DummySecretKey();
    }

}
