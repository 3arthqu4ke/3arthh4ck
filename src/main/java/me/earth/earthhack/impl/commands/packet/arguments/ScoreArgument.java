package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyScore;
import net.minecraft.scoreboard.Score;

public class ScoreArgument extends AbstractArgument<Score>
{
    public ScoreArgument()
    {
        super(Score.class);
    }

    @Override
    public Score fromString(String argument) throws ArgParseException
    {
        return new DummyScore();
    }

}
