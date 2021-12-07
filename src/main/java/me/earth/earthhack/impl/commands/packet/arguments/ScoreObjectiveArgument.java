package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyScoreObjective;
import net.minecraft.scoreboard.ScoreObjective;

public class ScoreObjectiveArgument extends AbstractArgument<ScoreObjective>
{
    public ScoreObjectiveArgument()
    {
        super(ScoreObjective.class);
    }

    @Override
    public ScoreObjective fromString(String argument) throws ArgParseException
    {
        return new DummyScoreObjective();
    }

}
