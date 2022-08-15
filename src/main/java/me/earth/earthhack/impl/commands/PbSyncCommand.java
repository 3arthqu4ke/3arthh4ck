package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.modules.SyncModule;

import java.util.ConcurrentModificationException;

public class PbSyncCommand extends AbstractModuleCommand {
    private static final SyncModule SYNC = new SyncModule();

    public PbSyncCommand() {
        super(new String[][]{{"pbsync"}, {"module"}, {"-reverse"}}, 1);
        CommandDescriptions.register(this, "Syncs modules of your client" +
            " with the modules on the PingBypass.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length <= 1)
        {
            ChatUtil.sendMessage(TextColor.RED + " Please specify a module!");
            return;
        }

        boolean reverse = args.length > 2 && args[2].equalsIgnoreCase("-reverse");
        ModuleManager from = reverse ? PingBypass.MODULES : Managers.MODULES;
        ModuleManager to = reverse ? Managers.MODULES : PingBypass.MODULES;

        Module module = from.getObject(args[1]);
        if (module == null)
        {
            ChatUtil.sendMessage(
                reverse
                    ? (TextColor.RED + "Module " + TextColor.WHITE
                        + args[1] + TextColor.RED
                        + " is not available on the PingBypass server!")
                    : TextColor.RED + " Could not find module "
                        + TextColor.WHITE + args[1] + TextColor.RED + "!");
            return;
        }

        Module pbModule = to.getObject(args[1]);
        if (pbModule == null)
        {
            ChatUtil.sendMessage(
                reverse
                    ? TextColor.RED + " Could not find module "
                        + TextColor.WHITE + args[1] + TextColor.RED + "!"
                    : TextColor.RED + "Module " + TextColor.WHITE
                        + args[1] + TextColor.RED
                        + " is not available on the PingBypass server!");
            return;
        }

        ChatUtil.sendMessage(TextColor.GREEN + "Syncing " + TextColor.WHITE
            + module.getName() + TextColor.GREEN + " with PingBypass.");

        try {
            SyncModule.sync(module, pbModule);
        } catch (ConcurrentModificationException e) {
            ModuleUtil.sendMessage(SYNC,
                                   TextColor.DARK_RED + e.getMessage());
        }
    }

}
