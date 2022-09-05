package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public class BlockHighlight extends BlockESPModule
{
    protected final Setting<Boolean> distance =
            register(new BooleanSetting("Distance", false));
    protected final Setting<Boolean> hitVec =
            register(new BooleanSetting("HitVec", false));
    protected final Setting<Boolean> position =
            register(new BooleanSetting("Position", false));
    protected final Setting<Boolean> eyes =
            register(new BooleanSetting("Eyes", false));
    protected final Setting<Boolean> slide =
            register(new BooleanSetting("Slide", false));
    protected final Setting<Boolean> toCenter =
            register(new BooleanSetting("ToCenter", false));
    protected final Setting<Integer> slideTime =
            register(new NumberSetting<>("Slide-Time", 100, 0, 1_000));

    protected final StopWatch slideTimer = new StopWatch();
    protected AxisAlignedBB currentBB;
    protected AxisAlignedBB slideBB;
    /** Name of the current mc.objectMouseOver. */
    protected String current;

    public BlockHighlight()
    {
        super("BlockHighlight", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerMotion(this));
        this.setData(new SimpleData(this,
                "Highlights the block that you are currently looking at."));
        this.unregister(this.height);
        this.color.setValue(new Color(0, 0, 0, 0));
    }

    @Override
    protected void onDisable()
    {
        currentBB = null;
        slideBB = null;
    }

    @Override
    public String getDisplayInfo()
    {
        return current;
    }

}
