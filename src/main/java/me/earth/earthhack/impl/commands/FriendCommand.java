package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractPlayerManagerCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.TextColor;

/**
 * A command for {@link Managers#FRIENDS}.
 */
public class FriendCommand extends AbstractPlayerManagerCommand
{
    /**
     * Constructs a new FriendCommand.
     * @see AbstractPlayerManagerCommand
     */
    public FriendCommand()
    {
        super(Managers.FRIENDS,
                "friend",
                "Friends",
                "friended",
                "a friend",
                TextColor.AQUA);
        CommandDescriptions.register(this, "Manage your friends.");
    }

}
