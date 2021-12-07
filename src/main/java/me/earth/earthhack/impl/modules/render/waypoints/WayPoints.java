package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointRender;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Set;

public class WayPoints extends RegisteringModule<BlockPos, WayPointSetting>
{
    protected final Setting<WayPointRender> render =
            register(new EnumSetting<>("Render", WayPointRender.None));
    protected final Setting<Boolean> ovwInNether =
            register(new BooleanSetting("OVW-Nether", false));
    protected final Setting<Boolean> netherOvw =
            register(new BooleanSetting("Nether-OVW", false));
    protected final Setting<Color> ovwColor =
            register(new ColorSetting("OVW-Color", new Color(0, 255, 0)));
    protected final Setting<Color> netherColor =
            register(new ColorSetting("Nether-Color", new Color(255, 255, 0)));
    protected final Setting<Color> endColor =
            register(new ColorSetting("End-Color", new Color(0, 255, 255)));
    protected final Setting<Float> scale         =
            register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));
    protected final Setting<Float> lineWidth =
            register(new NumberSetting<>("LineWidth", 1.5f, 0.1f, 5.0f));
    protected final Setting<Integer> range =
            register(new NumberSetting<>("Range", 1000, 0, Integer.MAX_VALUE));

    public WayPoints()
    {
        super("Waypoints",
                Category.Render,
                "Add Waypoint",
                "name> <type> <x> <y> <z",// (descriptor surrounds with < and >)
                s -> new WayPointSetting(s, BlockPos.ORIGIN),
                s -> "A waypoint.");

        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected PossibleInputs getInput(String input, String[] args)
    {
        if (args.length < 2)
        {
            PossibleInputs inputs = super.getInput(input, args);
            if (args.length == 1 && "del".startsWith(args[0].toLowerCase()))
            {
                inputs.setRest(" <name>");
            }

            return inputs;
        }

        PossibleInputs inputs = PossibleInputs.empty();
        if (args[0].equalsIgnoreCase("del"))
        {
            if (args.length > 2)
            {
                return inputs;
            }

            return inputs.setCompletion(getInput(input.substring(4), false));
        }

        if (!args[0].equalsIgnoreCase("add"))
        {
            return inputs;
        }

        switch (args.length)
        {
            case 2:
                return inputs.setRest(" <type> <x> <y> <z>");
            case 3:
                Enum<?> entry = EnumHelper.getEnumStartingWith(args[2],
                        WayPointType.class);
                if (entry == null || entry == WayPointType.None)
                {
                    return inputs.setRest(TextColor.RED +" unrecognized type!");
                }

                return inputs.setCompletion(TextUtil.substring(entry.toString(),
                                                              args[2].length()))
                             .setRest(" <x> <y> <z>");
            case 4:
                return inputs.setRest(" <y> <z>");
            case 5:
                return inputs.setRest(" <z>");
            default:
        }

        return inputs;
    }

    @Override
    public void add(String string)
    {
        if (string == null || string.isEmpty())
        {
            ChatUtil.sendMessage(TextColor.RED + "No WayPoint given!");
            return;
        }

        String[] args = CommandUtil.toArgs(string);
        if (args.length != 5)
        {
            ChatUtil.sendMessage(TextColor.RED
                        + "Expected 5 arguments: (Name, Type, X, Y, Z)," +
                        " but found "
                        + args.length
                        + "!");
            return;
        }

        WayPointType type = WayPointType.fromString(args[1]);
        if (type == WayPointType.None)
        {
            ChatUtil.sendMessage(TextColor.RED + "Can't recognize type "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + "! Try OVW, End or Nether!");
            return;
        }

        double x;
        double y;
        double z;

        try
        {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);
        }
        catch (Exception e)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't parse input to X,Y,Z Coordinates!");
            return;
        }

        WayPointSetting setting = addSetting(args[0]);
        if (setting != null)
        {
            setting.setType(type);
            setting.setValue(new BlockPos(x, y, z));
            ChatUtil.sendMessage(TextColor.GREEN
                                + "Added new waypoint: "
                                + TextColor.WHITE
                                + args[0]
                                + TextColor.GREEN
                                + ".");
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "A Waypoint called "
                    + TextColor.WHITE
                    + args[0] + TextColor.RED
                    + " already exists!");
        }
    }

    protected Color getColor(WayPointType type)
    {
        switch (type)
        {
            case OVW:
                return ovwColor.getValue();
            case End:
                return endColor.getValue();
            case Nether:
                return netherColor.getValue();
            default:
        }

        return Color.WHITE;
    }

    protected Set<WayPointSetting> getWayPoints()
    {
        return added;
    }

}
