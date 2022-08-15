package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketChatMessage;

final class ListenerMotion extends ModuleListener<Suicide, MotionUpdateEvent>
{
    public ListenerMotion(Suicide module)
    {
        super(module, MotionUpdateEvent.class, 10_000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.displaying)
        {
            return;
        }

        if (mc.player.getHealth() <= 0.0f)
        {
            module.disable();
            return;
        }

        if (module.mode.getValue() == SuicideMode.Command)
        {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            module.disable();
            return;
        }

        if (!module.autoCrystal.isEnabled())
        {
            module.autoCrystal.enable();
        }

        module.autoCrystal.switching = true;
        if (module.throwAwayTotem.getValue()
            && InventoryUtil.validScreen()
            && module.timer.passed(module.throwDelay.getValue())
            && mc.player.getHeldItemOffhand().getItem()
                == Items.TOTEM_OF_UNDYING)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                mc.playerController.windowClick(
                    0, 45, 1, ClickType.THROW, mc.player));
            module.timer.reset();
        }
    }

}
