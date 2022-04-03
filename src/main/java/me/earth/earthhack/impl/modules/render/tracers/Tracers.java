package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.tracers.mode.BodyPart;
import me.earth.earthhack.impl.modules.render.tracers.mode.TracerMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class Tracers extends EntityTypeModule
{
    protected final Setting<Boolean> items =
            register(new BooleanSetting("Items", false));
    protected final Setting<Boolean> invisibles =
            register(new BooleanSetting("Invisibles", false));
    protected final Setting<Boolean> friends    =
            register(new BooleanSetting("Friends", true));
    protected final Setting<TracerMode> mode    =
            register(new EnumSetting<>("Mode", TracerMode.Outline));
    protected final Setting<BodyPart> target    =
            register(new EnumSetting<>("Target", BodyPart.Body));
    protected final Setting<Boolean> lines      =
            register(new BooleanSetting("Lines", true));
    protected final Setting<Boolean> outsideFov      =
            register(new BooleanSetting("OutsideFov", false));
    protected final Setting<Float> lineWidth    =
            register(new NumberSetting<>("LineWidth", 1.5f, 0.1f, 5.0f));
    protected final Setting<Integer> tracers    =
            register(new NumberSetting<>("Amount", 100, 1, 250));
    protected final Setting<Float> minRange     =
            register(new NumberSetting<>("MinRange", 0.0f, 0.0f, 1000.0f));
    protected final Setting<Float> maxRange     =
            register(new NumberSetting<>("MaxRange", 1000.0f, 0.0f, 1000.0f));

    protected List<Entity> sorted = new ArrayList<>();

    public Tracers()
    {
        super("Tracers", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTick(this));
        this.setData(new TracersData(this));
    }

    @Override
    public boolean isValid(Entity entity)
    {
        if (entity != null
                && !EntityUtil.isDead(entity)
                && entity != mc.getRenderViewEntity()
                && (mc.getRenderViewEntity() == null
                    || !entity.equals(mc.getRenderViewEntity()
                                        .getRidingEntity())))
        {
            if (entity.getDistanceSq(mc.player)
                        < MathUtil.square(minRange.getValue())
                    || entity.getDistanceSq(mc.player)
                        > MathUtil.square(maxRange.getValue()))
            {
                return false;
            }

            if (outsideFov.getValue() && RotationUtil.inFov(entity))
            {
                return false;
            }

            if (items.getValue() && entity instanceof EntityItem)
            {
                return true;
            }

            if (players.getValue() && entity instanceof EntityPlayer
                    && (invisibles.getValue()
                            || !entity.isInvisible())
                    && (friends.getValue()
                            || !Managers.FRIENDS.contains(entity.getName())))
            {
                return true;
            }

            return super.isValid(entity);
        }

        return false;
    }

}
