package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.api.module.data.DefaultData;

final class ChatData extends DefaultData<Chat>
{
    public ChatData(Chat module)
    {
        super(module);
        register(module.noScroll, "Won't scroll the chat when its" +
                " scrolled and you receive new messages.");
        register(module.timeStamps, "Displays timestamps in chat.");
        register("Clean",
                "Doesn't render the rectangle around/behind the chat.");
        register("Infinite", "Never delete received messages.");
        register("Rainbow", "If you want Rainbow timestamps. Note that because"
            + " I'm lazy changing this setting will only take effect for" +
            " messages which have been received afterwards.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Tweaks for the chat.";
    }

}
