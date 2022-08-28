package me.earth.earthhack.impl.modules.client.accountspoof;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

import java.util.UUID;

public class AccountSpoof extends Module {
    protected final Setting<String> accountName =
        register(new StringSetting("AccountName", "SpoofAccountName"));
    protected final Setting<String> uuid =
        register(new StringSetting("AccountUUID", UUID.randomUUID().toString()));

    public AccountSpoof() {
        super("AccountSpoof", Category.Client);
        this.listeners.add(new ListenerLoginStart(this));
        this.setData(new SimpleData(this, "Spoofs your name and uuid on cracked servers."));
    }

}
