package me.earth.backup;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SuppressWarnings("unused")
public class BackupPlugin implements Plugin {
    @Override
    public void load() {
        Earthhack.getLogger().info("BackupPlugin loaded!");
        try {
            Managers.COMMANDS.register(new Command(new String[][]{{"backup-files"}}) {
                @Override
                public void execute(String[] args) {
                    backup();
                }
            });
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }

    public static void backup() {
        Managers.CHAT.sendDeleteMessage("Backing up configs...", "backup", ChatIDs.COMMAND);
        try {
            Files.walk(Paths.get("earthhack"))
                 .forEach(source -> {
                     Path destination = Paths.get("earthbackup", source.toString().substring("earthhack".length()));
                     try {
                         Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });

            Managers.CHAT.sendDeleteMessage(TextColor.GREEN + "Backed up configs successfully!", "backup", ChatIDs.COMMAND);
        } catch (IOException e) {
            Managers.CHAT.sendDeleteMessage(TextColor.RED + "Failed to backup configs: " + e.getMessage(), "backup", ChatIDs.COMMAND);
            e.printStackTrace();
        }
    }

}
