package me.earth.earthhack.tweaker.launch.arguments;

import com.google.gson.JsonElement;

public class BooleanArgument extends AbstractArgument<Boolean>
{
    public BooleanArgument()
    {
        this(Boolean.TRUE);
    }

    public BooleanArgument(Boolean initial)
    {
        super(initial);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        value = element.getAsBoolean();
    }

    @Override
    public String toJson()
    {
        return value.toString();
    }

}
