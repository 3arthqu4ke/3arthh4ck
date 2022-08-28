package me.earth.earthhack.impl.modules.render.popchams;

import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerTotemPop extends ModuleListener<PopChams, TotemPopEvent> {
    public ListenerTotemPop(PopChams module) {
        super(module, TotemPopEvent.class);
    }

    @Override
    public void invoke(TotemPopEvent event) {
        if (!module.isValidEntity(event.getEntity()))
            return;
        EntityPlayer player = event.getEntity();

        module.getPopDataList().add(new PopChams.PopData(PlayerUtil.copyPlayer(event.getEntity(), module.copyAnimations.getValue()),
                        System.currentTimeMillis(),
                        player.posX,
                        player.posY,
                        player.posZ,
                        player instanceof AbstractClientPlayer && ((AbstractClientPlayer)player).getSkinType().equals("slim") ));
    }
}
