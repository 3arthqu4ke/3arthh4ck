package me.earth.earthhack.impl.modules.client.pbteleport;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.pingbypass.PingBypass;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PbTeleport extends Module {
    private static final ModuleCache<PbTeleport> INSTANCE =
        Caches.getModule(PbTeleport.class);
    private static final ModuleCache<PacketFly> PACKET_FLY =
        Caches.getModule(PacketFly.class);

    protected final Setting<Boolean> async =
        register(new BooleanSetting("Async", false));
    protected final Setting<Boolean> performMotionUpdates =
        register(new BooleanSetting("Perform-Motion-Updates", false));
    protected final Setting<Boolean> spoofRotations =
        register(new BooleanSetting("Spoof-Rotations", true));
    protected final Setting<Boolean> enableSurround =
        register(new BooleanSetting("Enable-Surround", false));
    protected final Setting<Boolean> blockLag =
        register(new BooleanSetting("BlockLag", false));

    protected final AtomicInteger currentId = new AtomicInteger();
    protected final AtomicBoolean blocking = new AtomicBoolean();

    public PbTeleport() {
        super("PB-Teleport", Category.Client);
        SimpleData data = new SimpleData(this, "This handles lag backs on the PingBypass proxy. For crystalpvp.cc and maybe other servers that check your packet order.");
        data.register(async, "Handles the LagBack instantly, which might save a few ms but could cause problems.");
        data.register(performMotionUpdates, "If you want the modules of the PingBypass to still update while you are desynced.");
        data.register(spoofRotations, "If Perform-Motion-Updates should spoof your rotations.");
        data.register(blockLag, "If you want to burrow after the lagback.");
        data.register(enableSurround, "If you want to enable Surround after the lagback.");
        this.setData(data);
        if (PingBypass.isServer()) {
            this.listeners.add(new ListenerPosLook(this));
        }
    }

    @Override
    protected void onEnable() {
        blocking.set(false);
    }

    @Override
    protected void onDisable() {
        blocking.set(false);
    }

    public void onConfirm(int id) {
        synchronized (currentId) {
            if (currentId.get() == id) {
                blocking.set(false);
            }
        }
    }

    public static boolean isActive() {
        return PingBypass.isConnected()
            && !PingBypass.PACKET_SERVICE.isPacketFlying()
            && !PACKET_FLY.isEnabled()
            && INSTANCE.isEnabled();
    }

    public static boolean isBlocking() {
        return isActive() && INSTANCE.get().blocking.get();
    }

    public static boolean shouldPerformMotionUpdate() {
        return INSTANCE.isPresent() && INSTANCE.get().performMotionUpdates.getValue();
    }

    public static boolean shouldSpoofRotations() {
        return INSTANCE.isPresent() && INSTANCE.get().spoofRotations.getValue();
    }

}
