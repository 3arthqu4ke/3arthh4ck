package me.earth.earthhack.impl.modules.misc.spammer;

import me.earth.earthhack.api.module.data.DefaultData;

final class SpammerData extends DefaultData<Spammer>
{
    public SpammerData(Spammer module)
    {
        super(module);
        register(module.delay,
                "The interval in seconds that messages get sent in.");
        register(module.random,
                "Randomly selects a line from your spammer file.");
        register(module.antiKick,
                "Tricks antispams by appending a random suffix.");
        register(module.greenText, "Prepends a \">\".");
        register(module.refresh, "Refreshes the spammer file" +
                " under earthhack/util.");
        register(module.autoOff,
                "Turns this module off after it has sent a message.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Spams messages.";
    }

}
