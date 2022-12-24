package me.earth.earthhack.impl.modules.client.accountspoof;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketLoginStart;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.login.client.CPacketLoginStart;

import java.util.UUID;

final class ListenerLoginStart extends ModuleListener<AccountSpoof, PacketEvent.Send<CPacketLoginStart>> {
    public ListenerLoginStart(AccountSpoof module) {
        super(module, PacketEvent.Send.class, CPacketLoginStart.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketLoginStart> event) {
        if (mc.isSingleplayer() && !module.spoofSP.getValue()) {
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(module.uuid.getValue());
        } catch (IllegalArgumentException e) {
            ChatUtil.sendMessageScheduled(TextColor.RED + "Bad UUID for AccountSpoof: " + e.getMessage());
            uuid = UUID.randomUUID();
        }

        ((ICPacketLoginStart) event.getPacket()).setProfile(new GameProfile(uuid, module.accountName.getValue()));
    }

}
