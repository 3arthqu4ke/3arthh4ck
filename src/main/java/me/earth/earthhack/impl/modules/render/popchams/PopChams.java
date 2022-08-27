package me.earth.earthhack.impl.modules.render.popchams;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    public final BooleanSetting copyAnimations =
            register(new BooleanSetting("Copy-Animations", true));
    public final NumberSetting<Double> yAnimations =
            register(new NumberSetting<>("Y-Animation", 0., -7., 7.));
    protected final Setting<Boolean> friendPop =
            register(new BooleanSetting("Friend-Pop", false));
    public final ColorSetting friendColor =
            register(new ColorSetting("Friend-Color", new Color(45, 255, 45, 80)));
    public final ColorSetting friendOutline =
            register(new ColorSetting("Friend-Outline", new Color(45, 255, 45, 255)));
    private final List<PopData> popDataList = new ArrayList<>();

    public PopChams()
    {
        super("PopChams", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTotemPop(this));
        super.color.setValue(new Color(255, 45, 45, 80));
        super.outline.setValue(new Color(255, 45, 45, 255));
        this.unregister(super.height);
    }

    public List<PopData> getPopDataList() {
        return popDataList;
    }

    protected Color getColor(EntityPlayer entity) {
        if (entity.equals(mc.player)) {
            return selfColor.getValue();
        } else if (Managers.FRIENDS.contains(entity)) {
            return friendColor.getValue();
        } else {
            return color.getValue();
        }
    }

    protected Color getOutlineColor(EntityPlayer entity) {
        if (entity.equals(mc.player)) {
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
        private final StaticModelPlayer model;
        private final long time;
        private final double x;
        private final double y;
        private final double z;

        public PopData(EntityPlayer player, long time, double x, double y, double z, boolean slim) {
            this.player = player;
            this.time = time;
            this.x = x;
            this.y = y - (player.isSneaking() ? 0.125 : 0);
            this.z = z;
            this.model = new StaticModelPlayer(player, slim, 0);
            this.model.disableArmorLayers();
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
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

        public StaticModelPlayer getModel() {
            return model;
        }
    }
}
