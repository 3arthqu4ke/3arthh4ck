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

package me.earth.baritoneplugin;

import me.earth.baritoneplugin.commands.BaritoneCommand;
import me.earth.baritoneplugin.commands.GotoCommand;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class BaritonePlugin implements Plugin
{
    @Override
    public void load()
    {
        try
        {
            Managers.COMMANDS.register(new BaritoneCommand());
            Managers.COMMANDS.register(new GotoCommand());
        }
        catch (AlreadyRegisteredException e)
        {
            throw new IllegalStateException(e);
        }
    }

}
