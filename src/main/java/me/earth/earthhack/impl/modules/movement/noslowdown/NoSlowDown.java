package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.block.MixinBlockSoulSand;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MixinBlockSoulSand} for SoulSand.
 */
public class NoSlowDown extends Module
{
    protected final Setting<Boolean> guiMove  =
            register(new BooleanSetting("GuiMove", true));
    protected final Setting<Boolean> items    =
            register(new BooleanSetting("Items", true));
    protected final Setting<Boolean> legit    =
            register(new BooleanSetting("Legit", false));
    protected final Setting<Boolean> sprint   =
            register(new BooleanSetting("Sprint", true));
    protected final Setting<Boolean> input    =
            register(new BooleanSetting("Input", true));
    protected final Setting<Boolean> sneakPacket    =
            register(new BooleanSetting("SneakPacket", false));
    protected final Setting<Double> websY     =
            register(new NumberSetting<>("WebsVertical", 2.0, 1.0, 100.0));
    protected final Setting<Double> websXZ    =
            register(new NumberSetting<>("WebsHorizontal", 1.1, 1.0, 100.0));
    protected final Setting<Boolean> sneak    =
            register(new BooleanSetting("WebsSneak", false));
    protected final Setting<Boolean> useTimerWeb    =
            register(new BooleanSetting("UseTimerInWeb", false));
    protected final Setting<Double> timerSpeed     =
            register(new NumberSetting<>("Timer", 8.0, 0.1, 20.0));
    protected final Setting<Boolean> onGroundSpoof    =
            register(new BooleanSetting("OnGroundSpoof", false));
    protected final Setting<Boolean> superStrict =
            register(new BooleanSetting("SuperStrict", false));
    protected final Setting<Boolean> phobosGui =
            register(new BooleanSetting("PhobosGui", false));

    protected final List<Class<? extends GuiScreen>> screens =
            new ArrayList<>();

    protected final KeyBinding[] keys;
    protected boolean spoof = true;

    public NoSlowDown()
    {
        super("NoSlowDown", Category.Movement);
        register(new BooleanSetting("SoulSand", true));

        keys = new KeyBinding[]
        {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
        };

        screens.add(GuiOptions.class);
        screens.add(GuiVideoSettings.class);
        screens.add(GuiScreenOptionsSounds.class);
        screens.add(GuiContainer.class);
        screens.add(GuiIngameMenu.class);

        this.listeners.add(new ListenerSprint(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPostKeys(this));
        this.listeners.add(new ListenerRightClickItem(this));
        this.listeners.add(new ListenerTryUseItem(this));
        this.listeners.add(new ListenerTryUseItemOnBlock(this));
        this.setData(new NoSlowDownData(this));
    }

    @Override
    protected void onDisable()
    {
        Managers.NCP.setStrict(false);
    }

    protected void updateKeyBinds()
    {
        if (guiMove.getValue())
        {
            if (screens
                    .stream()
                    .anyMatch(screen -> screen.isInstance(mc.currentScreen))
                || phobosGui.getValue() && mc.currentScreen instanceof Click)
            {
                for (KeyBinding key : keys)
                {
                    KeyBinding.setKeyBindState(key.getKeyCode(),
                                               KeyBoardUtil.isKeyDown(key));
                }
            }
            else if (mc.currentScreen == null)
            {
                for (KeyBinding key : keys)
                {
                    if (!KeyBoardUtil.isKeyDown(key))
                    {
                        KeyBinding.setKeyBindState(key.getKeyCode(), false);
                    }
                }
            }
        }
    }

    protected void onPacket(CPacketPlayer packet) {
        /*if (onGroundSpoof.getValue()) {
            ((ICPacketPlayer) packet).setOnGround(false);
            ((ICPacketPlayer) packet).setY(packet.getY(mc.player.posY) + 0.1);
        }*/
    }

}
