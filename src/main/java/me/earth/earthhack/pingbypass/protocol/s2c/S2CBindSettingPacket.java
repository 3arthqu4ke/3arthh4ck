package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CBindSettingPacket extends S2CPacket implements Globals {
    private String moduleName;
    private String settingName;
    private int keyCode;
    private int id;

    public S2CBindSettingPacket() {
        super(ProtocolIds.S2C_BIND_SETTING);
    }

    public S2CBindSettingPacket(String moduleName, String setting, int key, int id) {
        super(ProtocolIds.S2C_BIND_SETTING);
        this.moduleName = moduleName;
        this.settingName = setting;
        this.keyCode = key;
        this.id = id;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.moduleName = buf.readString(Short.MAX_VALUE);
        this.settingName = buf.readString(Short.MAX_VALUE);
        this.keyCode = buf.readVarInt();
        this.id = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeString(moduleName);
        buf.writeString(settingName);
        buf.writeVarInt(keyCode);
        buf.writeVarInt(id);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            Module module = PingBypass.MODULES.getObject(moduleName);
            if (module != null) {
                Setting<?> setting = module.getSetting(settingName);
                if (setting instanceof BindSetting && setting.changeId.get() == id) {
                    mc.addScheduledTask(() -> {
                        if (setting.changeId.get() == id) {
                            ((BindSetting) setting).setValue(Bind.fromKey(keyCode));
                        }
                    });
                }
            }
        });
    }

}
