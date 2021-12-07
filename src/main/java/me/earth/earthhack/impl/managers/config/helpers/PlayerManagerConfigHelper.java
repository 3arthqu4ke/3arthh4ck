package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.managers.client.PlayerManager;
import me.earth.earthhack.impl.managers.config.util.PlayerConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

public class PlayerManagerConfigHelper
        extends AbstractConfigHelper<PlayerConfig>
{
    private final PlayerManager manager;

    public PlayerManagerConfigHelper(String name,
                                     String path,
                                     PlayerManager manager)
    {
        super(name, path);
        this.manager = manager;
    }

    @Override
    protected PlayerConfig create(String name)
    {
        return PlayerConfig.fromManager(name, manager);
    }

    @Override
    protected JsonObject toJson(PlayerConfig config)
    {
        return config.getAsJsonObject();
    }

    @Override
    protected PlayerConfig readFile(InputStream stream, String name)
    {
        PlayerConfig config = new PlayerConfig(name, manager);
        JsonObject object = Jsonable.PARSER
                                    .parse(new InputStreamReader(stream))
                                    .getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            config.register(entry.getKey(),
                            UUID.fromString(entry.getValue().getAsString()));
        }

        return config;
    }

}
