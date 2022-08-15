package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SCommandPacket;

public class ProxyCommand extends Command implements Globals
{
    private static final ModuleCache<PingBypassModule> MODULE =
        Caches.getModule(PingBypassModule.class);

    public ProxyCommand()
    {
        super(new String[][]{{"proxy"}, {"command"}});
        CommandDescriptions.register(this, "Send commands to your PingBypass.");
    }

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null || !MODULE.isEnabled())
        {
            ChatUtil.sendMessage(TextColor.RED + "You need to be connected" +
                                     " to a PingBypass server for this" +
                                     " command to work!");
            return;
        }
        
        if (args.length < 2)
        {
            ChatUtil.sendMessage(
                TextColor.RED + "You need to specify a command to send" +
                    " to the PingBypass proxy.");
            return;
        }
        
        if (MODULE.get().isOld())
        {
            /* TODO old protocol
            
            String command;
            if (args[1].equalsIgnoreCase("friend")) {
                StringBuilder sb = new StringBuilder("@ServerFriend ");
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                
                mc.player.connection.sendPacket(
                    new CPacketChatMessage(sb.toString()));
            } else {
                StringBuilder sb = new StringBuilder("@Server ");
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
            } */
            
            ChatUtil.sendMessage(TextColor.RED
                                     + "PingBypass is on the old protocol!");
            return;
        }

        String[] array = getActualArgs(args);
        mc.player.connection.sendPacket(new C2SCommandPacket(array));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length <= 1) {
            return super.getPossibleInputs(args);
        }

        String[] array = getActualArgs(args);
        return Managers.COMMANDS.getCommandForMessage(array)
                                .getPossibleInputs(array);
    }

    private String[] getActualArgs(String[] args) {
        if (args.length == 0) {
            return new String[0];
        }

        String[] array = new String[args.length - 1];
        System.arraycopy(args, 1, array, 0, array.length);
        return array;
    }

}
