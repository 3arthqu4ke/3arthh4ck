package me.earth.futurefriends;

import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class FutureFriendsPlugin implements Plugin {
    @Override
    public void load() {
        Earthhack.getLogger().info("Loading FutureFriends plugin!");
        try {
            Managers.COMMANDS.register(new FutureFriendsCommand());
        } catch (AlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }

}
