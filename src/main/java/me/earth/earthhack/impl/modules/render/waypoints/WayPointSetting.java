package me.earth.earthhack.impl.modules.render.waypoints;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;
import me.earth.earthhack.impl.util.helpers.addable.setting.RemovingSetting;
import net.minecraft.util.math.BlockPos;

public class WayPointSetting extends RemovingSetting<BlockPos>
{
    static
    {
        ComponentFactory.register(WayPointSetting.class, WayPointComponent::new);
    }

    private WayPointType type = WayPointType.None;
    private boolean corrupted;

    public WayPointSetting(String nameIn, BlockPos initialValue)
    {
        super(nameIn, initialValue);
    }

    @Override
    public void setValue(BlockPos value)
    {
        this.value = value;
    }

    @Override
    public void setValue(BlockPos value, boolean withEvent)
    {
        this.value = value;
    }

    @Override
    public void fromJson(JsonElement element)
    {
        String[] s = element.getAsString().split("[xyz]");

        if (s.length < 4)
        {
            corrupted = true;
            Earthhack.getLogger().warn(this.getName()
                                        + " : can't set from Json: "
                                        + element.getAsString()
                                        + ".");
            return;
        }

        this.type = WayPointType.fromString(s[0]);
        if (type == WayPointType.None)
        {
            Earthhack.getLogger()
                     .warn(this.getName() + " corrupted type: " + s[0]);
            corrupted = true;
            return;
        }

        try
        {
            double x = Double.parseDouble(s[1]);
            double y = Double.parseDouble(s[2]);
            double z = Double.parseDouble(s[3]);

            this.setValue(new BlockPos(x, y, z));
        }
        catch (Exception e)
        {
            corrupted = true;
            e.printStackTrace();
        }
    }

    @Override
    public String toJson()
    {
        BlockPos pos = this.getValue();
        return type + "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();
    }

    @Override
    public Setting<BlockPos> copy() {
        return new WayPointSetting(getName(), getInitial());
    }

    public boolean isCorrupted()
    {
        return corrupted;
    }

    public WayPointType getType()
    {
        return type;
    }

    public void setType(WayPointType type)
    {
        this.type = type;
    }

}
