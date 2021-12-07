package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerRemove extends
        ModuleListener<Spectate, PacketEvent.Receive<SPacketDestroyEntities>>
{
    public ListenerRemove(Spectate module)
    {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event)
    {
        if (module.spectating)
        {
            EntityPlayer player = module.player;
            if (player != null)
            {
                for (int id : event.getPacket().getEntityIDs())
                {
                    if (id == player.getEntityId())
                    {
                        mc.addScheduledTask(() ->
                        {
                            module.disable();
                            ModuleUtil.sendMessage(module, TextColor.RED
                                + "The Player you spectated got removed.");
                        });

                        return;
                    }
                }
            }
        }
    }

}
