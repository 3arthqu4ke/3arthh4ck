package me.earth.earthhack.impl.modules.combat.selftrap;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

// Maybe extend?
public class SelfTrap extends ObbyListenerModule<ListenerSelfTrap>
{
    protected final Setting<SelfTrapMode> mode =
            register(new EnumSetting<>("Mode", SelfTrapMode.Obsidian));
    protected final Setting<Boolean> smart =
            register(new BooleanSetting("Smart", false));
    protected final Setting<Float> range =
            register(new NumberSetting<>("SmartRange", 6.0f, 0.0f, 20.0f));
    protected final Setting<Double> placeRange =
            register(new NumberSetting<>("PlaceRange", 6.0, 0.0, 7.5));
    protected final Setting<Integer> maxHelping =
            register(new NumberSetting<>("HelpingBlocks", 4, 0, 20));
    protected final Setting<Boolean> autoOff =
            register(new BooleanSetting("Auto-Off", true));
    protected final Setting<Boolean> smartOff =
            register(new BooleanSetting("Smart-Off", true));
    protected final Setting<Boolean> prioBehind =
            register(new BooleanSetting("Prio-Behind", true));

    protected BlockPos startPos;

    public SelfTrap()
    {
        super("SelfTrap", Category.Combat);
    }

    @Override
    protected void onEnable()
    {
        Entity entity = RotationUtil.getRotationPlayer();
        if (entity != null)
        {
            startPos = PositionUtil.getPosition(entity);
        }

        super.onEnable();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        startPos = null;
    }

    @Override
    public boolean execute()
    {
        if (mode.getValue() != SelfTrapMode.Obsidian)
        {
            attacking = null;
        }

        return super.execute();
    }

    @Override
    protected ListenerSelfTrap createListener()
    {
        return new ListenerSelfTrap(this);
    }

    @Override
    public EntityPlayer getPlayerForRotations()
    {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    protected boolean entityCheckSimple(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean entityCheck(BlockPos pos)
    {
        return true; // ???
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos)
    {
        return false;
    }

}
