package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.impl.core.mixins.entity.living.IEntityFireworkRocket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends ModuleListener<NCPTweaks, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(NCPTweaks module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (!module.elytraFix.getValue()) {
            return;
        }

        mc.addScheduledTask(() -> {
            if (mc.player == null || mc.world == null) {
                return;
            }
            ItemStack stack = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (stack.getItem() == Items.ELYTRA && ItemElytra.isUsable(stack) && mc.player.isElytraFlying()) {
                for (EntityFireworkRocket e : mc.world.getEntities(EntityFireworkRocket.class, e -> ((IEntityFireworkRocket)e).getBoostedEntity() == mc.player)) {
                    mc.world.removeEntity(e);
                }
            }
        });
    }

}
