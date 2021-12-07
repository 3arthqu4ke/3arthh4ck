package me.earth.earthhack.impl.modules.render.popchams;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;

public class PopChams extends BlockESPModule
{
    protected final Setting<Integer> fadeTime =
            register(new NumberSetting<>("Fade-Time", 1500, 0, 5000));
    protected final Setting<Boolean> selfPop =
            register(new BooleanSetting("Self-Pop", false));
    public final ColorSetting selfColor =
            register(new ColorSetting("Self-Color", new Color(80, 80, 255, 80)));
    public final ColorSetting selfOutline =
            register(new ColorSetting("Self-Outline", new Color(80, 80, 255, 255)));
    protected final Setting<Boolean> friendPop =
            register(new BooleanSetting("Friend-Pop", false));
    public final ColorSetting friendColor =
            register(new ColorSetting("Friend-Color", new Color(45, 255, 45, 80)));
    public final ColorSetting friendOutline =
            register(new ColorSetting("Friend-Outline", new Color(45, 255, 45, 255)));
    private final HashMap<String,PopData> popDataHashMap = new HashMap<>();

    public PopChams()
    {
        super("PopChams", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTotemPop(this));
        super.color.setValue(new Color(255, 45, 45, 80));
        super.outline.setValue(new Color(255, 45, 45, 255));
        this.unregister(super.height);
    }

    @Override
    protected void onEnable()
    {
        popDataHashMap.clear();
    }

    public HashMap<String, PopData> getPopDataHashMap() {
        return popDataHashMap;
    }

    protected Color getColor(EntityPlayer entity) {
        if (entity == mc.player) {
            return selfColor.getValue();
        } else if (Managers.FRIENDS.contains(entity)) {
            return friendColor.getValue();
        } else {
            return color.getValue();
        }
    }

    protected Color getOutlineColor(EntityPlayer entity) {
        if (entity == mc.player) {
            return selfOutline.getValue();
        } else if (Managers.FRIENDS.contains(entity)) {
            return friendOutline.getValue();
        } else {
            return outline.getValue();
        }
    }

    protected boolean isValidEntity(EntityPlayer entity) {
        return !(entity == mc.player && !selfPop.getValue()) && !((Managers.FRIENDS.contains(entity) && entity != mc.player) && !friendPop.getValue());
    }

    public static class PopData {
        private final EntityPlayer player;
        private final long time;
        private final float yaw;
        private final float pitch;
        private final double x;
        private final double y;
        private final double z;

        public PopData(EntityPlayer player, long time, float yaw, float pitch, double x, double y, double z) {
            this.player = player;
            this.time = time;
            this.yaw = yaw;
            this.pitch = pitch;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
