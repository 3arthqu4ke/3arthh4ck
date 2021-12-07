package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPostPlace extends ModuleListener<AutoCrystal,
        PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerPostPlace(AutoCrystal module)
    {
        super(module,
                PacketEvent.Post.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event)
    {
        if (module.idPredict.getValue()
            && !module.noGod
            && module.breakTimer.passed(module.breakDelay.getValue())
            && mc.player
                 .getHeldItem(event.getPacket().getHand())
                 .getItem() == Items.END_CRYSTAL
            && module.idHelper.isSafe(Managers.ENTITIES.getPlayersAsync(),
                                      module.holdingCheck.getValue(),
                                      module.toolCheck.getValue()))
        {
            module.idHelper.attack(module.breakSwing.getValue(),
                                   module.godSwing.getValue(),
                                   module.idOffset.getValue(),
                                   module.idPackets.getValue(),
                                   module.idDelay.getValue());

            module.breakTimer.reset(module.breakDelay.getValue());
        }
    }

}
