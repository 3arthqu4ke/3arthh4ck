package me.earth.earthhack.impl.commands.packet.util;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScorePlayerTeam extends ScorePlayerTeam implements Dummy
{
    public DummyScorePlayerTeam()
    {
        super(new Scoreboard(), "Dummy-ScorePlayerTeam");
    }

}
