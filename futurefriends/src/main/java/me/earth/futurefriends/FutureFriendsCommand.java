package me.earth.futurefriends;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.io.*;
import java.nio.file.Paths;

public class FutureFriendsCommand extends Command {
    public FutureFriendsCommand() {
        super(new String[][]{{"futurefriends"}, {"file"}});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.sendMessage(TextColor.RED
                                     + "Please specify a file to read!");
            return;
        }

        File file = Paths.get(args[1]).toFile();
        if (!file.exists() || file.isDirectory()) {
            ChatUtil.sendMessage(
                TextColor.RED + "File " + TextColor.WHITE + file + TextColor.RED
                    + " either doesn't exist or is a directory!");
        }

        try (InputStream is = new FileInputStream(file)) {
            JsonArray array = Jsonable.PARSER.parse(new InputStreamReader(is))
                                             .getAsJsonArray();
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject jo = element.getAsJsonObject();
                JsonElement friend = jo.get("friend-label");
                if (friend != null) {
                    Managers.LOOK_UP.doLookUp(
                        new LookUp(LookUp.Type.UUID, friend.getAsString()) {
                            public void onSuccess() {
                                Managers.FRIENDS.add(name, uuid);
                                Managers.CHAT.sendDeleteMessageScheduled(
                                    TextColor.AQUA + name + TextColor.GREEN + " was added as a friend.",
                                    name, ChatIDs.PLAYER_COMMAND);
                            }

                            public void onFailure() {
                                ChatUtil.sendMessageScheduled(
                                    TextColor.RED + "Failed to find " + name);
                            }
                        });
                }
            }
        } catch (IOException | JsonParseException | IllegalStateException e) {
            ChatUtil.sendMessage(TextColor.RED + "Couldn't parse file: "
                                     + TextColor.WHITE
                                     + file + TextColor.RED
                                     + ": " + e.getMessage());
        }
    }

}
