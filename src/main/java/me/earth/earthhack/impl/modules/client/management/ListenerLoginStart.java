package me.earth.earthhack.impl.modules.client.management;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketLoginStart;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.login.client.CPacketLoginStart;

import java.util.UUID;

final class ListenerLoginStart
    extends ModuleListener<Management, PacketEvent.Send<CPacketLoginStart>> {
    public ListenerLoginStart(Management module) {
        super(module, PacketEvent.Send.class, CPacketLoginStart.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketLoginStart> event) {
        if (module.accountSpoof.getValue()) {
            ((ICPacketLoginStart) event.getPacket()).setProfile(new GameProfile(
                UUID.randomUUID(), module.accountName.getValue()));
        }
    }

}
