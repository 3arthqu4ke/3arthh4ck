package me.earth.earthhack.impl.modules.render.popchams;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
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
        EntityPlayer playerCopy = new EntityPlayer(mc.world, new GameProfile(UUID.randomUUID(), player.getName())) {
            @Override public boolean isSpectator() {
                return false;
            }

            @Override public boolean isCreative() {
                return false;
            }
        };

        if (module.copyAnimations.getValue()) {
            playerCopy.setSneaking(player.isSneaking());
            playerCopy.limbSwing = player.limbSwing;
            playerCopy.limbSwingAmount = player.prevLimbSwingAmount;
        }
        playerCopy.setEntityId(player.getEntityId());
        playerCopy.copyLocationAndAnglesFrom(player);
        module.getPopDataList().add(new PopChams.PopData(playerCopy,
                        System.currentTimeMillis(),
                        event.getEntity().rotationYaw,
                        event.getEntity().rotationPitch,
                        event.getEntity().posX,
                        event.getEntity().posY,
                        event.getEntity().posZ
        ));
    }
}
