package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.impl.core.mixins.network.client.ICPacketClientSettings;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketClientSettings;

final class ListenerSettings extends
        ModuleListener<SettingSpoof, PacketEvent.Send<CPacketClientSettings>>
{
    public ListenerSettings(SettingSpoof module)
    {
        super(module, PacketEvent.Send.class, CPacketClientSettings.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketClientSettings> event)
    {
        ICPacketClientSettings p = (ICPacketClientSettings) event.getPacket();
        p.setLang(module.getLanguage(p.getLang()));
        p.setView(module.getRenderDistance(p.getView()));
        p.setChatVisibility(module.getVisibility(p.getChatVisibility()));
        p.setEnableColors(module.getChatColors(p.getEnableColors()));
        p.setModelPartFlags(module.getModelParts(p.getModelPartFlags()));
        p.setMainHand(module.getHandSide(p.getMainHand()));
    }

}
