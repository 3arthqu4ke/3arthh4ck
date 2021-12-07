package me.earth.earthhack.impl.managers.config.util;

import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.IdentifiedNameable;
import me.earth.earthhack.impl.managers.client.PlayerManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerConfig extends IdentifiedNameable implements Config
{
    private final Map<String, UUID> players = new ConcurrentHashMap<>();
    private final PlayerManager manager;

    public PlayerConfig(String name, PlayerManager manager)
    {
        super(name);
        this.manager = manager;
    }

    public void register(String name, UUID uuid)
    {
        players.put(name, uuid);
    }

    @Override
    public void apply()
    {
        manager.clear();
        for (Map.Entry<String, UUID> entry : players.entrySet())
        {
            manager.add(entry.getKey(), entry.getValue());
        }
    }

    public JsonObject getAsJsonObject()
    {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, UUID> entry : players.entrySet())
        {
            object.add(entry.getKey(),
                       Jsonable.parse(entry.getValue().toString()));
        }

        return object;
    }

    public static PlayerConfig fromManager(String name, PlayerManager p)
    {
        PlayerConfig config = new PlayerConfig(name, p);
        for (Map.Entry<String, UUID> entry : p.getPlayersWithUUID().entrySet())
        {
            config.register(entry.getKey(), entry.getValue());
        }

        return config;
    }

}
