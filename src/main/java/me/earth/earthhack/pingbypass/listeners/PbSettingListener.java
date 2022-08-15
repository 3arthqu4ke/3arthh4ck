package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CBindSettingPacket;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CSettingPacket;

public class PbSettingListener extends EventListener<SettingEvent.Post<?>> {
    public PbSettingListener() {
        super(SettingEvent.Post.class);
    }

    @Override
    public void invoke(SettingEvent.Post<?> event) {
        if (PingBypass.isConnected()) {
            SettingContainer module = event.getSetting().getContainer();
            if (module instanceof Nameable) {
                if (event.getSetting() instanceof BindSetting) {
                    PingBypass.sendPacket(
                        new S2CBindSettingPacket(
                            ((Nameable) module).getName(),
                            event.getSetting().getName(),
                            ((BindSetting) event.getSetting())
                                .getValue().getKey(),
                            event.getSetting().changeId.get()));
                } else {
                    PingBypass.sendPacket(
                        new S2CSettingPacket(
                            ((Nameable) module).getName(),
                            event.getSetting(), event.getSetting().toJson(),
                            event.getSetting().changeId.get()));
                }
            }
        }
    }

}
