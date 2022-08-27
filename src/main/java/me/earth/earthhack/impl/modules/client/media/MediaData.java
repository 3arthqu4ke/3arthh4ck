package me.earth.earthhack.impl.modules.client.media;

import me.earth.earthhack.api.module.data.DefaultData;

final class MediaData extends DefaultData<Media>
{
    public MediaData(Media module)
    {
        super(module);
        register("Replacement", "The name you want to appear with.");
        register("Reload", "Use this in case you changed your" +
                " name using an altmanager.");
        register(module.replaceCustom, "Renders custom names.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to protect your name when recording or streaming.";
    }

}
