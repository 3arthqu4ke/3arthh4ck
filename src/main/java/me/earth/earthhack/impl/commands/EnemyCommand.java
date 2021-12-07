package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractPlayerManagerCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.TextColor;

/**
 * A command for {@link Managers#ENEMIES}.
 */
public class EnemyCommand extends AbstractPlayerManagerCommand
{
    /**
     * Constructs a new EnemyCommand.
     * @see AbstractPlayerManagerCommand
     */
    public EnemyCommand()
    {
        super(Managers.ENEMIES,
                "enemy",
                "Enemies",
                "enemied",
                "an enemy",
                TextColor.RED);
        CommandDescriptions.register(this, "Manage your enemies.");
    }

}
