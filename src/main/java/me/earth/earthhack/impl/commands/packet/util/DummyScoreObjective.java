package me.earth.earthhack.impl.commands.packet.util;

import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScoreObjective extends ScoreObjective implements Dummy
{
    public DummyScoreObjective()
    {
        super(new Scoreboard(), "Dummy-Objective", IScoreCriteria.DUMMY);
    }

}
