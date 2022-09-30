package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.MobEffects;

public class AutoSprint extends Module
{
    protected final Setting<SprintMode> mode =
            register(new EnumSetting<>("Mode", SprintMode.Rage));

    public AutoSprint()
    {
        super("Sprint", Category.Movement);
        this.listeners.add(new LambdaListener<>(
            UpdateEvent.class, e -> onTick()));
        this.setData(new AutoSprintData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    protected void onDisable()
    {
        KeyBinding.setKeyBindState(
                        mc.gameSettings.keyBindSprint.getKeyCode(),
                        KeyBoardUtil.isKeyDown(mc.gameSettings.keyBindSprint));
    }

    public SprintMode getMode()
    {
        return mode.getValue();
    }

    public void onTick()
    {
        if ((canSprint()
            && (mode.getValue() == SprintMode.Legit))
            || (AutoSprint.canSprintBetter()
            && (mode.getValue() == SprintMode.Rage)))
        {
            mode.getValue().sprint();
        }
    }

    public static boolean canSprint()
    {
        return mc.player != null
                && !mc.player.isSneaking()
                && !mc.player.collidedHorizontally
                && MovementUtil.isMoving()
                && ((mc.player.getFoodStats().getFoodLevel() > 6.0f
                    || mc.player.capabilities.allowFlying)
                        && !mc.player.isPotionActive(MobEffects.BLINDNESS));
    }

    public static boolean canSprintBetter()
    {
        return (mc.gameSettings.keyBindForward.isKeyDown()
                    || mc.gameSettings.keyBindBack.isKeyDown()
                    || mc.gameSettings.keyBindLeft.isKeyDown()
                    || mc.gameSettings.keyBindRight.isKeyDown())
                && !(mc.player == null
                    || mc.player.isSneaking()
                    || mc.player.collidedHorizontally
                    || mc.player.getFoodStats().getFoodLevel() <= 6f);
    }

}
