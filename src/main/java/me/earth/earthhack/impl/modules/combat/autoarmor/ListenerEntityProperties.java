package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.play.server.SPacketEntityProperties;

final class ListenerEntityProperties extends
        ModuleListener<AutoArmor, PacketEvent.Receive<SPacketEntityProperties>>
{
    public ListenerEntityProperties(AutoArmor module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityProperties.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityProperties> event)
    {
        EntityPlayerSP player = mc.player;
        if (player != null
                && event.getPacket().getEntityId() == player.getEntityId())
        {
            for (SPacketEntityProperties.Snapshot snapShot :
                    event.getPacket().getSnapshots())
            {
                if (snapShot.getName()
                            .equals(SharedMonsterAttributes.ARMOR.getName()))
                {
                    // TODO: might not required anymore
                    module.propertyTimer.reset();
                    break;
                }
            }
        }
    }

}
