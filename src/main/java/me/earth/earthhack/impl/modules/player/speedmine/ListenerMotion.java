package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;

final class ListenerMotion extends ModuleListener<Speedmine, MotionUpdateEvent>
{
    private static final ModuleCache<Nuker> NUKER =
        Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
        Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerMotion(Speedmine module)
    {
        super(module, MotionUpdateEvent.class, 999);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.rotate.getValue()
            || PingBypass.isConnected() && !event.isPingBypass())
        {
            return;
        }

        Packet<?> packet = module.limitRotationPacket;
        if (event.getStage() == Stage.PRE
                && module.pos != null
                && !PlayerUtil.isCreative(mc.player)
                && (!NUKER.isEnabled() || !NUKE.getValue())
                && (!InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE)
                    || mc.gameSettings.keyBindUseItem.isKeyDown())
                && (!module.limitRotations.getValue()
                    || packet != null))
        {
            module.rotations = RotationUtil
                    .getRotations(module.pos, module.facing);
            event.setYaw(module.rotations[0]);
            event.setPitch(module.rotations[1]);
        }
        else if (event.getStage() == Stage.POST
                    && module.limitRotations.getValue()
                    && packet != null)
        {
            boolean toAir = module.toAir.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int last = mc.player.inventory.currentItem;
                int slot = module.limitRotationSlot;
                module.cooldownBypass.getValue().switchTo(slot);

                if (module.event.getValue())
                {
                    mc.player.connection.sendPacket(packet);
                }
                else
                {
                    NetworkUtil.sendPacketNoEvent(packet, false);
                }

                module.cooldownBypass.getValue().switchBack(last, slot);
            });

            module.onSendPacket();
            module.limitRotationPacket = null;
            module.limitRotationSlot = -1;
            module.postSend(toAir);
        }
    }

}
