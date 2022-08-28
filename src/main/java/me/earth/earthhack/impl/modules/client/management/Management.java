package me.earth.earthhack.impl.modules.client.management;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.entity.EntityPlayerSP;

import java.awt.*;

/**
 * {@link me.earth.earthhack.impl.core.mixins.util.MixinScreenShotHelper}
 * {@link me.earth.earthhack.forge.mixins.minecraftforge.MixinGameData}
 */
public class Management extends Module
{
    protected final Setting<Boolean> clear =
        register(new BooleanSetting("ClearPops", false));
    protected final Setting<Boolean> logout =
        register(new BooleanSetting("LogoutPops", false));
    protected final Setting<Boolean> friend =
        register(new BooleanSetting("SelfFriend", true));
    protected final Setting<Boolean> soundRemove =
        register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Integer> deathTime =
        register(new NumberSetting<>("DeathTime", 250, 0, 1000));
    protected final Setting<Integer> time =
        register(new NumberSetting<>("Time", 0, 0, 24000));
    protected final Setting<Boolean> aspectRatio =
        register(new BooleanSetting("ChangeAspectRatio", false));
    protected final Setting<Integer> aspectRatioWidth =
        register(new NumberSetting<>("AspectRatioWidth", mc.displayWidth, 0, mc.displayWidth));
    protected final Setting<Integer> aspectRatioHeight =
        register(new NumberSetting<>("AspectRatioHeight", mc.displayHeight, 0, mc.displayHeight));
    protected final Setting<Boolean> pooledScreenShots =
        register(new BooleanSetting("Pooled-Screenshots", false));
    protected final Setting<Boolean> pauseOnLeftFocus =
        register(new BooleanSetting("PauseOnLeftFocus",
                                     mc.gameSettings.pauseOnLostFocus));
    protected final Setting<Boolean> customFogColor =
            register(new BooleanSetting("CustomFogColor", false));
    protected final Setting<Color> fogColor =
            register(new ColorSetting("FogColor", new Color(255, 255, 255, 255)));
    protected final Setting<Boolean> resourceDebug =
            register(new BooleanSetting("ResourceDebug", false)); // TODO:
    protected final Setting<Integer> unfocusedFps =
            register(new NumberSetting<>("UnfocusedFps", 30, 1, 1000));
    protected final Setting<CooldownBypass> globalCooldownBypass =
            register(new EnumSetting<>("Global-CD-Bypass",
                                       CooldownBypass.None));
    protected final Setting<CooldownBypass> manualCooldownBypass =
            register(new EnumSetting<>("Manual-CD-Bypass",
                                        CooldownBypass.None));

    protected GameProfile lastProfile;
    protected EntityPlayerSP player;

    public Management()
    {
        super("Management", Category.Client);
        Bus.EVENT_BUS.register(new ListenerLogout(this));
        Bus.EVENT_BUS.register(new ListenerGameLoop(this));
        Bus.EVENT_BUS.register(new ListenerAspectRatio(this));
        Bus.EVENT_BUS.register(new ListenerTick(this));
        Bus.EVENT_BUS.register(new ListenerSwitch(this));
        register(new NumberSetting<>("PB-Position-Range", 5.0, 0.0, 10_000.0));
        register(new BooleanSetting("MotionService", true))
            .setComplexity(Complexity.Expert);
        register(new NumberSetting<>("EntityTracker-Updates", 2.0, 0.01, 1000.0))
            .setComplexity(Complexity.Expert);
        // TODO: kinda ugly that this is here
        register(new BooleanSetting("PB-SetPos", true))
            .setComplexity(Complexity.Expert);
        register(new BooleanSetting("PB-FixChunks", false))
            .setComplexity(Complexity.Expert);

        this.setData(new ManagementData(this));
        this.clear.addObserver(event ->
        {
            event.setValue(false);
            ChatUtil.sendMessage("Clearing TotemPops...");
            Managers.COMBAT.reset();
        });
        this.pauseOnLeftFocus.addObserver(e ->
            mc.gameSettings.pauseOnLostFocus = e.getValue());
        register(new BooleanSetting("IgnoreForgeRegistries", false));
    }

    @Override
    protected void onLoad()
    {
        if (friend.getValue())
        {
            lastProfile = mc.getSession().getProfile();
            Managers.FRIENDS.add(lastProfile.getName(), lastProfile.getId());
        }
    }

    public boolean isUsingCustomFogColor()
    {
        return customFogColor.getValue();
    }

    public Color getCustomFogColor()
    {
        return fogColor.getValue();
    }

}
