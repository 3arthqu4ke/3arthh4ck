package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.api.module.data.DefaultData;

final class NoAFKData extends DefaultData<NoAFK>
{
    public NoAFKData(NoAFK module)
    {
        super(module);
        register("Rotate", "Makes you rotate.");
        register("Swing", "Makes you swing your arm.");
        register("Sneak", "Makes you sneak and unsneak.");
        register("AutoReply", "Makes you reply to /msg's.");
        register("Message", "The message to reply with.");
        register("Indicator", "Private Message, depends on server.");
        register("Reply", "Private response, depends on server.");
        register("Color", "Indicator, depends on server, most servers make " +
                          "private messages LightPurple.");
        register(module.baritone, "This needs baritone installed.");
    }

    @Override
    public int getColor()
    {
        return 0xff1e4E99;
    }

    @Override
    public String getDescription()
    {
        return "Prevents you from getting kicked for being afk, can also be " +
                "used as AutoReply.";
    }

}
