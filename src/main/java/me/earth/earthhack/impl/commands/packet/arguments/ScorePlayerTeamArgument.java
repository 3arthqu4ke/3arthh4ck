package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyScorePlayerTeam;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ScorePlayerTeamArgument extends AbstractArgument<ScorePlayerTeam>
{
    public ScorePlayerTeamArgument()
    {
        super(ScorePlayerTeam.class);
    }

    @Override
    public ScorePlayerTeam fromString(String argument) throws ArgParseException
    {
        return new DummyScorePlayerTeam();
    }

}
