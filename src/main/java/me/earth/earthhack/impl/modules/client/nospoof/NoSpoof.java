package me.earth.earthhack.impl.modules.client.nospoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.util.client.SimpleData;

public class NoSpoof extends Module {
    private static final ModuleCache<NoSpoof> CACHE =
        Caches.getModule(NoSpoof.class);

    private final Setting<Boolean> position =
        register(new BooleanSetting("Position", true));
    private final Setting<Boolean> rotation =
        register(new BooleanSetting("Rotation", true));
    private final Setting<Boolean> onGround =
        register(new BooleanSetting("OnGround", true));

    public NoSpoof() {
        super("NoSpoof", Category.Client);
        this.setData(new SimpleData(this, "Signalizes to the PingBypass server"
            + " that it should not spoof rotations."));
    }

    public static boolean noPosition() {
        return CACHE.isEnabled() && CACHE.get().position.getValue();
    }

    public static boolean noRotation() {
        return CACHE.isEnabled() && CACHE.get().rotation.getValue();
    }

    public static boolean noGround() {
        return CACHE.isEnabled() && CACHE.get().onGround.getValue();
    }

}
