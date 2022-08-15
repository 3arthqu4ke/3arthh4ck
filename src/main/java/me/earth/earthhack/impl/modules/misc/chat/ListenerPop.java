package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

final class ListenerPop extends ModuleListener<Chat, TotemPopEvent> {
    public ListenerPop(Chat module) {
        super(module, TotemPopEvent.class);
    }

    @Override
    public void invoke(TotemPopEvent event) {
        EntityPlayerSP player = mc.player;
        //noinspection ConstantConditions
        if (module.popMessage.getValue()
            && module.popLagTimer.passed(module.popLagDelay.getValue())
            && player != null
            && !player.equals(event.getEntity())
            && event.getEntity() instanceof EntityPlayer
            && event.getEntity().getName() != null
            && !Managers.FRIENDS.contains(event.getEntity())
            && module.sent.add(event.getEntity().getName()))
        {
            String name = event.getEntity().getName();
            player.connection.sendPacket(new CPacketChatMessage(
                "/msg " + name + " " + Chat.LAG_MESSAGE));
            module.popLagTimer.reset();
        }
    }

}
