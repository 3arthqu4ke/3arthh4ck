package me.earth.earthhack.pingbypass.protocol.s2c;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CSettingPacket extends S2CPacket implements Globals {
    private String moduleName;
    private String settingName;
    private String value;
    private int id;

    public S2CSettingPacket() {
        super(ProtocolIds.S2C_SETTING);
    }

    public S2CSettingPacket(String moduleName, Setting<?> setting, String value, int id) {
        super(ProtocolIds.S2C_SETTING);
        this.moduleName = moduleName;
        this.settingName = setting.getName();
        this.value = value;
        this.id = id;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.moduleName = buf.readString(Short.MAX_VALUE);
        this.settingName = buf.readString(Short.MAX_VALUE);
        this.value = buf.readString(Short.MAX_VALUE);
        this.id = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeString(moduleName);
        buf.writeString(settingName);
        buf.writeString(value);
        buf.writeVarInt(id);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        // TODO: make O(1) lookup by name...
        Module module = PingBypass.MODULES.getObject(moduleName);
        if (module != null) {
            Setting<?> setting = module.getSetting(settingName);
            if (setting != null && setting.changeId.get() == id) {
                JsonElement element = Jsonable.parse(value);
                mc.addScheduledTask(() -> {
                    if (setting.changeId.get() == id) {
                        setting.fromJson(element);
                    }
                });
            }
        }
    }

}
