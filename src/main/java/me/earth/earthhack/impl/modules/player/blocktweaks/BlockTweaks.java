package me.earth.earthhack.impl.modules.player.blocktweaks;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.pingbypass.input.Mouse;

//TODO: no entity block better
public class BlockTweaks extends RemovingItemAddingModule
{
    protected final Setting<Integer> delay       =
            register(new NumberSetting<>("BreakDelay", 0, 0, 5));
    protected final Setting<Boolean> noBreakAnim =
            register(new BooleanSetting("NoBreakAnim", false));
    protected final Setting<Boolean> entityMine  =
            register(new BooleanSetting("EntityMine", true));
    protected final Setting<Boolean> m1Attack    =
            register(new BooleanSetting("RightAttack", false));
    protected final Setting<Boolean> ignoreFalling    =
            register(new BooleanSetting("IgnoreFalling", false));
    protected final Setting<Boolean> newVerEntities =
            register(new BooleanSetting("1.13-Entities", false));

    public BlockTweaks()
    {
        super("BlockTweaks", Category.Player, s ->
                "Lets you mine through entities while holding " + s.getName());
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPacket(this));
        this.setData(new BlockTweaksData(this));
    }

    public boolean areNewVerEntitiesActive()
    {
        return this.isEnabled() && newVerEntities.getValue();
    }

    public boolean isIgnoreFallingActive()
    {
        return this.isEnabled() && ignoreFalling.getValue();
    }

    public boolean noMiningTrace()
    {
        return this.isEnabled()
                && entityMine.getValue()
                && this.isStackValid(mc.player.getHeldItemMainhand())
                && (!m1Attack.getValue() || !Mouse.isButtonDown(1));
    }

}
