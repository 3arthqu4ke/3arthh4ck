package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.HashSet;
import java.util.Set;

//TODO: Limit TileEntities
//TODO: NoRender Spectators
public class NoRender extends Module
{
    protected final Setting<Boolean> fire         =
            register(new BooleanSetting("Fire", true));
    protected final Setting<Boolean> entityFire         =
            register(new BooleanSetting("EntityFire", true));
    protected final Setting<Boolean> portal       =
            register(new BooleanSetting("Portal", true));
    protected final Setting<Boolean> pumpkin      =
            register(new BooleanSetting("Pumpkin", true));
    protected final Setting<Boolean> totemPops    =
            register(new BooleanSetting("TotemPop", true));
    protected final Setting<Boolean> nausea       =
            register(new BooleanSetting("Nausea", true));
    protected final Setting<Boolean> hurtCam      =
            register(new BooleanSetting("HurtCam", true));
    protected final Setting<Boolean> noWeather =
            register(new BooleanSetting("Weather", true));
    protected final Setting<Boolean> barriers     =
            register(new BooleanSetting("Barriers", false));
    protected final Setting<Boolean> skyLight     =
            register(new BooleanSetting("SkyLight", true));
    protected final Setting<Boolean> noFog        =
            register(new BooleanSetting("NoFog", true));
    protected final Setting<Boolean> blocks       =
            register(new BooleanSetting("Blocks", true));
    protected final Setting<Boolean> advancements =
            register(new BooleanSetting("Advancements", false));
    protected final Setting<Boolean> critParticles =
            register(new BooleanSetting("CritParticles", false));
    protected final Setting<Boolean> dynamicFov    =
            register(new BooleanSetting("DynamicFov", true));
    public final Setting<Boolean> boss         =
            register(new BooleanSetting("BossHealth", true));
    public final Setting<Boolean> explosions         =
            register(new BooleanSetting("Explosions", true));
    public final Setting<Boolean> defaultBackGround  =
            register(new BooleanSetting("DefaultGuiBackGround", false));
    protected final Setting<Boolean> items =
            register(new BooleanSetting("Items", false));
    protected final Setting<Boolean> helmet =
            register(new BooleanSetting("Helmet", false));
    protected final Setting<Boolean> chestplate =
            register(new BooleanSetting("Breastplate", false));
    protected final Setting<Boolean> leggings =
            register(new BooleanSetting("Leggings", false));
    protected final Setting<Boolean> boots =
            register(new BooleanSetting("Boots", false));
    protected final Setting<Boolean> entities =
            register(new BooleanSetting("Entities", false));
    public final Setting<Boolean> worldBorder =
        register(new BooleanSetting("WorldBorder", false));

    protected final Set<Integer> ids = new HashSet<>();

    public NoRender()
    {
        super("NoRender", Category.Render);
        this.listeners.add(new ListenerSuffocation(this));
        this.listeners.add(new ListenerAnimation(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRenderEntities(this));
        this.setData(new NoRenderData(this));
    }

    public boolean noFire()
    {
        return this.isEnabled() && fire.getValue();
    }

    public boolean noEntityFire()
    {
        return this.isEnabled() && entityFire.getValue();
    }

    public boolean noTotems()
    {
        return this.isEnabled() && totemPops.getValue();
    }

    public boolean noHurtCam()
    {
        return this.isEnabled() && hurtCam.getValue();
    }

    public boolean noPortal()
    {
        return this.isEnabled() && portal.getValue();
    }

    public boolean noPumpkin()
    {
        return this.isEnabled() && pumpkin.getValue();
    }

    public boolean noNausea()
    {
        return this.isEnabled() && nausea.getValue();
    }

    public boolean noFog()
    {
        return this.isEnabled() && noFog.getValue();
    }

    public boolean noSkyLight()
    {
        return this.isEnabled() && skyLight.getValue();
    }

    public boolean noAdvancements()
    {
        return this.isEnabled() && advancements.getValue();
    }

    public boolean noWeather()
    {
        return this.isEnabled() && noWeather.getValue();
    }

    public boolean showBarriers()
    {
        return this.isEnabled() && barriers.getValue();
    }

    public boolean dynamicFov()
    {
        return this.isEnabled() && dynamicFov.getValue();
    }

    public boolean isValidArmorPiece(EntityEquipmentSlot slot)
    {
        if (!this.isEnabled()) return true;
        if (slot == EntityEquipmentSlot.HEAD && helmet.getValue()) return false;
        if (slot == EntityEquipmentSlot.CHEST && chestplate.getValue()) return false;
        if (slot == EntityEquipmentSlot.LEGS && leggings.getValue()) return false;
        return slot != EntityEquipmentSlot.FEET || !boots.getValue();
    }

}
