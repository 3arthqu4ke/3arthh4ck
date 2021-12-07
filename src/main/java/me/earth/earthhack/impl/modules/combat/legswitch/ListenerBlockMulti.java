package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

import java.util.List;

final class ListenerBlockMulti extends
        ModuleListener<LegSwitch, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerBlockMulti(LegSwitch module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        SPacketMultiBlockChange p = event.getPacket();
        if (module.breakBlock.getValue()
                && (InventoryUtil.isHolding(Items.END_CRYSTAL)
                    || module.autoSwitch.getValue() != LegAutoSwitch.None)
                && (module.rotate.getValue() == ACRotate.None
                    || module.rotate.getValue() == ACRotate.Break))
        {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            for (SPacketMultiBlockChange.BlockUpdateData d :
                    p.getChangedBlocks())
            {
                if (module.isValid(d.getPos(), d.getBlockState(), players))
                {
                    event.addPostEvent(module::startCalculation);
                    return;
                }
            }
        }
    }

}
