package me.earth.earthhack.impl.modules.misc.autoeat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.client.settings.KeyBinding;

// TODO: Mode Combat!
// TODO: make this the best AutoEat ever!!!!
public class AutoEat extends Module
{
    protected final Setting<Float> hunger =
        register(new NumberSetting<>("Hunger", 19.0f, 0.1f, 19.0f));
    protected final Setting<Boolean> health =
        register(new BooleanSetting("Health", false));
    protected final Setting<Float> enemyRange =
        register(new NumberSetting<>("Enemy-Range", 0.0f, 0.0f, 24.0f));
    protected final Setting<Float> safeHealth =
        register(new NumberSetting<>("Safe-Health", 19.0f, 0.1f, 36.0f));
    protected final Setting<Float> unsafeHealth =
        register(new NumberSetting<>("Unsafe-Health", 19.0f, 0.1f, 36.0f));
    protected final Setting<Boolean> calcWithAbsorption =
        register(new BooleanSetting("CalcWithAbsorption", true));
    protected final Setting<Boolean> absorption =
        register(new BooleanSetting("Absorption", false));
    protected final Setting<Float> absorptionAmount =
        register(new NumberSetting<>("AbsorptionAmount", 0.0f, 0.0f, 16.0f));
    protected final Setting<Boolean> always =
        register(new BooleanSetting("Always", false));

    protected boolean isEating;
    protected boolean server;
    protected boolean force;
    protected int lastSlot;

    public AutoEat()
    {
        super("AutoEat", Category.Misc);
        this.listeners.add(new ListenerTick(this));
    }

    @Override
    protected void onEnable()
    {
        force    = false;
        server   = false;
        lastSlot = -1;
        isEating = false;
    }

    @Override
    protected void onDisable()
    {
        this.reset();
    }

    public void reset()
    {
        force    = false;
        server   = false;
        lastSlot = -1;
        isEating = false;
        KeyBinding.setKeyBindState(
                mc.gameSettings.keyBindUseItem.getKeyCode(),
                KeyBoardUtil.isKeyDown(mc.gameSettings.keyBindUseItem));
    }

    public boolean isEating()
    {
        return this.isEnabled() && isEating;
    }

    public void setServer(boolean server)
    {
        this.server = server;
    }

}
