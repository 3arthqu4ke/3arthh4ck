package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;

final class ListenerTick extends ModuleListener<NoSlowDown, TickEvent>
{
    public ListenerTick(NoSlowDown module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        Managers.NCP.setStrict(module.guiMove.getValue()
                                    && module.legit.getValue());
        if (event.isSafe()
                && module.legit.getValue()
                && module.items.getValue())
        {
            Item item = mc.player.getActiveItemStack().getItem();
            if (MovementUtil.isMoving()
                    && item instanceof ItemFood
                        || item instanceof ItemBow
                        || item instanceof ItemPotion)
            {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                        mc.player.getPosition(),
                        EnumFacing.DOWN));
            }
            if (!mc.player.isHandActive()
                    && Managers.ACTION.isSprinting()
                    && module.sneakPacket.getValue())
            {
                mc.player.connection.sendPacket(
                    new CPacketEntityAction(
                            mc.player,
                            CPacketEntityAction.Action.STOP_SPRINTING));
            }

            if (((IEntity) mc.player).inWeb()
                    && !mc.player.onGround
                    && module.useTimerWeb.getValue())
            {
                Managers.TIMER.setTimer(
                    module.timerSpeed.getValue().floatValue());
                module.usingTimer = true;
            } else if (module.usingTimer) {
                Managers.TIMER.reset();
                module.usingTimer = false;
            }
        }
    }

}
