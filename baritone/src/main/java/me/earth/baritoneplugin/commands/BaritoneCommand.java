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
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;

public class BaritoneCommand extends Command
{
    public BaritoneCommand()
    {
        super(new String[][]{{"baritone"}, {"command"}});
        CommandDescriptions.register(this,
                "Allows you to send a baritone command.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length <= 1)
        {
            BaritoneAPI.getProvider()
                       .getPrimaryBaritone()
                       .getCommandManager()
                       .execute("baritone");
            return;
        }

        String conc = CommandUtil.concatenate(args, 1);
        BaritoneAPI.getProvider()
                   .getPrimaryBaritone()
                   .getCommandManager()
                   .execute(conc);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length <= 1)
        {
            return super.getPossibleInputs(args);
        }

        String conc = CommandUtil.concatenate(args, 1);
        String completion = BaritoneUtil.complete(conc);
        if (completion != null)
        {
            return new PossibleInputs(
                TextUtil.substring(completion, args[args.length - 1].length()),
                "");
        }

        return PossibleInputs.empty();
    }

}
