package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

final class ListenerMove extends ModuleListener<BowSpam, MoveEvent> {
    private float lastTimer = -1.f;

    public ListenerMove(BowSpam module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ItemStack stack = getStack();
        if (module.spam.getValue() && mc.player.onGround && stack != null && !mc.player.getActiveItemStack().isEmpty() && mc.player.getItemInUseCount() > 0) {
            event.setX(0);
            event.setY(0);
            event.setZ(0);
            mc.player.setVelocity(0, 0, 0);
        }
    }

    private ItemStack getStack() {
        ItemStack mainHand = mc.player.getHeldItemMainhand();

        if (mainHand.getItem() instanceof ItemBow) {
            return mainHand;
        }

        ItemStack offHand = mc.player.getHeldItemOffhand();

        if (offHand.getItem() instanceof ItemBow) {
            return offHand;
        }

        return null;
    }

}
