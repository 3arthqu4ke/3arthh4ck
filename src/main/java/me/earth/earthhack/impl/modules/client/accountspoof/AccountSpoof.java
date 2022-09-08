package me.earth.earthhack.impl.modules.client.accountspoof;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.client.SimpleData;

import java.util.UUID;
import java.util.regex.Pattern;

public class AccountSpoof extends Module {
    protected final Setting<String> accountName =
        register(new StringSetting("AccountName", "SpoofAccountName"));
    protected final Setting<String> uuid =
        register(new StringSetting("AccountUUID", UUID.randomUUID().toString()));
    protected final Setting<Boolean> spoofSP =
        register(new BooleanSetting("SpoofSinglePlayer", false));

    public Pattern pattern = Media.compileWithColorCodes("SpoofAccountName");

    public AccountSpoof() {
        super("AccountSpoof", Category.Client);
        this.listeners.add(new ListenerLoginStart(this));
        this.setData(new SimpleData(this, "Spoofs your name and uuid on cracked servers."));
        this.accountName.addObserver(e -> {
            if (!e.isCancelled()) {
                pattern = Media.compileWithColorCodes(e.getValue());
            }
        });
        register(new BooleanSetting("RandomUUID", false)).addObserver(e -> {
            if (e.getValue()) {
                e.setCancelled(true);
                uuid.setValue(UUID.randomUUID().toString());
            }
        });
    }

}
