package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;

public class VClipCommand extends Command implements Globals
{
    public VClipCommand()
    {
        super(new String[][]{{"vclip"}, {"amount"}});
        CommandDescriptions.register(this, "Teleports you vertically.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please specify an amount to be vclipped by.");
            return;
        }

        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command.");
            return;
        }

        double amount;
        try
        {
            amount = Double.parseDouble(args[1]);
            Entity entity = mc.player.getRidingEntity() != null
                                ? mc.player.getRidingEntity()
                                : mc.player;
            entity.setPosition(entity.posX, entity.posY + amount, entity.posZ);
            PacketUtil.doY(entity.posY + amount, mc.player.onGround);
            ChatUtil.sendMessage(TextColor.GREEN
                                    + "VClipped you "
                                    + TextColor.WHITE
                                    + args[1]
                                    + TextColor.GREEN
                                    + " blocks.");
        }
        catch (Exception e)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't parse "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + ", a number (can be a floating point one) is required.");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 1)
        {
            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

}
