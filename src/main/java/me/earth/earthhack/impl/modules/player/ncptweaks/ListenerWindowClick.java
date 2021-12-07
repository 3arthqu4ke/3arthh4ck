package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

final class ListenerWindowClick extends
        ModuleListener<NCPTweaks, PacketEvent.Send<CPacketClickWindow>>
{
    private final StopWatch timer = new StopWatch();

    public ListenerWindowClick(NCPTweaks module)
    {
        super(module, PacketEvent.Send.class, -1001, CPacketClickWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketClickWindow> event)
    {
        if (module.eating.getValue() && isEating())
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                mc.playerController.onStoppedUsingItem(mc.player));
        }

        if (module.moving.getValue())
        {
            if (module.packet.getValue()
                    && timer.passed(module.delay.getValue()))
            {
                float yaw = ((IEntityPlayerSP) mc.player).getLastReportedYaw();
                PacketUtil.doRotation(
                        (float) (yaw + 0.0004),
                        ((IEntityPlayerSP) mc.player).getLastReportedPitch(),
                        mc.player.onGround);
                timer.reset();
            }

            mc.player.setVelocity(0, 0, 0);
        }

        if (module.resetNCP.getValue())
        {
            Managers.NCP.reset();
        }
    }

    private boolean isEating()
    {
        ItemStack stack = mc.player.getActiveItemStack();
        if (mc.player.isHandActive() && !stack.isEmpty())
        {
            Item item = stack.getItem();
            if (item.getItemUseAction(stack) != EnumAction.EAT)
            {
                return false;
            }
            else
            {
                return item.getMaxItemUseDuration(stack)
                            - mc.player.getItemInUseCount() >= 5;
            }
        }
        else
        {
            return false;
        }
    }
    
}
