package me.earth.earthhack.impl.modules.render.popchams;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

final class ListenerTotemPop extends ModuleListener<PopChams, TotemPopEvent> {
    public ListenerTotemPop(PopChams module) {
        super(module, TotemPopEvent.class);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void invoke(TotemPopEvent event) {
        if (!module.isValidEntity(event.getEntity()))
            return;
        EntityPlayer player = event.getEntity();
        int useCount = player.getItemInUseCount();
        EntityPlayer playerCopy = new EntityPlayer(mc.world, new GameProfile(UUID.randomUUID(), player.getName())) {
            @Override public boolean isSpectator() {
                return false;
            }

            @Override public boolean isCreative() {
                return false;
            }

            @Override public int getItemInUseCount() {
                return useCount;
            }
        };

        if (module.copyAnimations.getValue()) {
            playerCopy.setSneaking(player.isSneaking());
            playerCopy.swingProgress = player.swingProgress;
            playerCopy.limbSwing = player.limbSwing;
            playerCopy.limbSwingAmount = player.prevLimbSwingAmount;
            playerCopy.inventory.copyInventory(player.inventory);
        }
        playerCopy.setPrimaryHand(player.getPrimaryHand());
        playerCopy.ticksExisted = player.ticksExisted;
        playerCopy.setEntityId(player.getEntityId());
        playerCopy.copyLocationAndAnglesFrom(player);
        module.getPopDataList().add(new PopChams.PopData(playerCopy,
                        System.currentTimeMillis(),
                        event.getEntity().rotationYaw,
                        event.getEntity().rotationPitch,
                        event.getEntity().posX,
                        event.getEntity().posY,
                        event.getEntity().posZ,
                        event.getEntity() instanceof AbstractClientPlayer && ((AbstractClientPlayer) event.getEntity()).getSkinType().equals("slim")
        ));
    }
}
