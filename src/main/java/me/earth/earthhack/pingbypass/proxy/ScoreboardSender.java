package me.earth.earthhack.pingbypass.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.List;
import java.util.Set;

public class ScoreboardSender {
    public static void sendScoreboard(Scoreboard scoreboardIn, NetworkManager manager)
    {
        if (scoreboardIn == null)
        {
            return;
        }

        Set<ScoreObjective> set = Sets.newHashSet();
        for (ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams())
        {
            manager.sendPacket(new SPacketTeams(scoreplayerteam, 0));
        }

        for (int i = 0; i < 19; ++i)
        {
            ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);
            if (scoreobjective != null && !set.contains(scoreobjective))
            {
                for (Packet<?> packet : getCreatePackets(scoreboardIn, scoreobjective))
                {
                    manager.sendPacket(packet);
                }

                set.add(scoreobjective);
            }
        }
    }

    private static List<Packet<?>> getCreatePackets(Scoreboard scoreBoard, ScoreObjective objective)
    {
        List <Packet<?>> list = Lists.newArrayList();
        list.add(new SPacketScoreboardObjective(objective, 0));

        for (int i = 0; i < 19; ++i)
        {
            if (scoreBoard.getObjectiveInDisplaySlot(i) == objective)
            {
                list.add(new SPacketDisplayObjective(i, objective));
            }
        }

        for (Score score : scoreBoard.getSortedScores(objective))
        {
            list.add(new SPacketUpdateScore(score));
        }

        return list;
    }

}
