package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<Chat, TickEvent>
{
    public static final BlockPos QUEUE_POS = new BlockPos(0, 240, 0);

    public ListenerTick(Chat module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe())
        {
            module.sent.clear();
        }

        if (event.isSafe()
                && module.autoQMain.getValue()
                && module.timer.passed(module.qDelay.getValue()))
        {
            ServerData data = mc.getCurrentServerData();
            if (data != null
                    && "2b2t.org".equalsIgnoreCase(data.serverIP) // TODO: also connect.2b2t.org?
                    && mc.player.dimension == 1
                    && mc.player.getPosition().equals(QUEUE_POS))
            {
                ChatUtil.sendMessage("<" + module.getDisplayName()
                    + "> Sending " + TextColor.RAINBOW
                    + module.message.getValue() + TextColor.RESET + "...");

                mc.player.sendChatMessage(module.message.getValue());
                module.timer.reset();
            }
        }
    }

}
