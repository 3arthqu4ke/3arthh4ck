package me.earth.earthhack.impl.modules.client.server.util;

import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class ChatLogger implements ILogger
{
    @Override
    public void log(String message)
    {
        ChatUtil.sendMessageScheduled(message);
    }

}