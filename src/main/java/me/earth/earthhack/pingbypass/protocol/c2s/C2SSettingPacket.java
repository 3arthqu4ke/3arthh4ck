package me.earth.earthhack.pingbypass.protocol.c2s;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SSettingPacket extends C2SPacket implements Globals {
    private String moduleName;
    private String settingName;
    private String value;
    private int id;

    public C2SSettingPacket() {
        super(ProtocolIds.C2S_SETTING);
    }

    public C2SSettingPacket(String moduleName, Setting<?> setting, String value, int id) {
        super(ProtocolIds.C2S_SETTING);
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
    public void execute(NetworkManager networkManager) throws IOException {
        // TODO: make O(1) lookup by name...
        Module module = Managers.MODULES.getObject(moduleName);
        if (module != null) {
            Setting<?> setting = module.getSetting(settingName);
            if (setting != null) {
                JsonElement element = Jsonable.parse(value);
                mc.addScheduledTask(() -> {
                    setting.fromJson(element);
                    setting.changeId.set(id);
                });
            }
        }
    }

}
