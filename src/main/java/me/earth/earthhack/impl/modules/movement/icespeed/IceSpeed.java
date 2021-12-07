package me.earth.earthhack.impl.modules.movement.icespeed;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module
{
    protected final Setting<Float> speed =
            register(new NumberSetting<>("Speed", 0.4f, 0.0f, 1.5f));

    public IceSpeed()
    {
        super("IceSpeed", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        SimpleData data = new SimpleData(this,
                "Makes you faster when walking on ice.");
        data.register(speed, "Modify your speed by this value.");
        this.setData(data);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDisable()
    {
        Blocks.ICE.slipperiness         = 0.98f;
        Blocks.PACKED_ICE.slipperiness  = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }

}
