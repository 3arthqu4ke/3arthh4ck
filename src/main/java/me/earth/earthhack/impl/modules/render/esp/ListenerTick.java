package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerTick extends ModuleListener<ESP, TickEvent> {
    public ListenerTick(ESP module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        module.phasing.clear();
        if (event.isSafe()) {
            if (module.phase.getValue()) {
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (!player.isDead && PhaseUtil.isPhasing(
                        player, module.pushMode.getValue())) {
                        module.phasing.add(player);
                    }
                }
            }
        }
    }

}
