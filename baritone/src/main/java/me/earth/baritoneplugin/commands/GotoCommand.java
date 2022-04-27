/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.earth.baritoneplugin.commands;

import baritone.api.BaritoneAPI;
import me.earth.baritoneplugin.util.BaritoneUtil;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.waypoints.WayPointSetting;
import me.earth.earthhack.impl.modules.render.waypoints.WayPoints;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.math.BlockPos;

public class GotoCommand extends Command implements Globals
{
    private static final ModuleCache<WayPoints> WAY_POINTS =
            Caches.getModule(WayPoints.class);

    public GotoCommand()
    {
        super(new String[][]{{"goto"},
                {"waypoint", "block", "x"}, {"y"}, {"z"}});
        CommandDescriptions.register(this,
                "Allows you to send a goto command.");
    }

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null || mc.world == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command!");
            return;
        }

        if (args.length <= 1)
        {
            ChatUtil.sendMessage(TextColor.RED +
                "Use this command to use baritones goto.");
            return;
        }

        WayPointSetting setting = getSettingStartingWith(args[1]);
        if (setting != null)
        {
            BlockPos pos = setting.getValue();
            BaritoneAPI.getProvider()
                       .getPrimaryBaritone()
                       .getCommandManager()
                       .execute("goto " + pos.getX() + " "
                                        + pos.getY() + " "
                                        + pos.getZ());
            return;
        }

        BaritoneAPI.getProvider()
                   .getPrimaryBaritone()
                   .getCommandManager()
                   .execute(CommandUtil.concatenate(args, 0));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length == 1)
        {
            return super.getPossibleInputs(args);
        }

        if (args.length == 2)
        {
            WayPointSetting s = getSettingStartingWith(args[1]);
            if (s != null)
            {
                return new PossibleInputs(
                    TextUtil.substring(s.getName(), args[1].length()), "");
            }

            boolean startsWithMinecraft = false;
            String[] concatenator = new String[2];
            concatenator[0] = args[0];
            if ("minecraft:".startsWith(args[1]))
            {
                concatenator[1] = "minecraft:";
                startsWithMinecraft = true;
            }
            else
            {
                concatenator[1] = args[1];
            }

            String conc = CommandUtil.concatenate(concatenator, 0);
            String completion = BaritoneUtil.complete(conc);
            if (completion != null)
            {
                if (startsWithMinecraft)
                {
                    return new PossibleInputs(
                        TextUtil.substring("minecraft:", args[1].length())
                            + completion.replaceFirst("minecraft:", ""), "");
                }

                return new PossibleInputs(
                    TextUtil.substring(completion, args[1].length()), "");
            }
        }

        return PossibleInputs.empty();
    }

    private WayPointSetting getSettingStartingWith(String text)
    {
        if (WAY_POINTS.isPresent())
        {
            for (Setting<?> setting : WAY_POINTS.get().getSettings())
            {
                if (setting instanceof WayPointSetting
                        && TextUtil.startsWith(setting.getName(), text))
                {
                    return (WayPointSetting) setting;
                }
            }
        }

        return null;
    }

}
