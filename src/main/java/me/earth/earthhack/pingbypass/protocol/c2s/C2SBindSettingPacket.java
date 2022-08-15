package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

/**
 * This is necessary because HeadlessMc-Lwjgl makes parsing binds impossible.
 */
public class C2SBindSettingPacket extends C2SPacket implements Globals {
    private String moduleName;
    private String settingName;
    private int keyCode;
    private int id;

    public C2SBindSettingPacket() {
        super(ProtocolIds.C2S_BIND_SETTING);
    }

    public C2SBindSettingPacket(String moduleName, String setting, int key, int id) {
        super(ProtocolIds.C2S_BIND_SETTING);
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
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            Module module = Managers.MODULES.getObject(moduleName);
            if (module != null) {
                Setting<?> setting = module.getSetting(settingName);
                if (setting instanceof BindSetting) {
                    mc.addScheduledTask(() -> {
                        ((BindSetting) setting).setValue(Bind.fromKey(keyCode));
                        setting.changeId.set(id);
                    });
                }
            }
        });
    }

}
