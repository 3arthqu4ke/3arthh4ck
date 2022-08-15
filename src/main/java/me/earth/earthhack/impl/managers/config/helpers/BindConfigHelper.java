package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.managers.config.util.BindConfig;
import me.earth.earthhack.impl.managers.config.util.BindWrapper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class BindConfigHelper extends AbstractConfigHelper<BindConfig>
{
    private final ModuleManager moduleManager;

    public BindConfigHelper()
    {
        this("bind", "binds", Managers.MODULES);
    }

    public BindConfigHelper(String name, String path, ModuleManager moduleManager)
    {
        super(name, path);
        this.moduleManager = moduleManager;
    }

    @Override
    protected BindConfig create(String name)
    {
        return BindConfig.create(name, moduleManager);
    }

    @Override
    protected JsonObject toJson(BindConfig config)
    {
        JsonObject object = new JsonObject();
        for (BindWrapper wrapper : config.getBinds())
        {
            String wrapped = Jsonable.GSON.toJson(wrapper);
            object.add(wrapper.getModule() + "-" + wrapper.getName(),
                    Jsonable.parse(wrapped, false));
        }

        return object;
    }

    @Override
    protected BindConfig readFile(InputStream stream, String name)
    {
        BindConfig config = new BindConfig(name, moduleManager);
        JsonObject object = Jsonable.PARSER
                                    .parse(new InputStreamReader(stream))
                                    .getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            config.add(Jsonable.GSON.fromJson(entry.getValue(),
                                              BindWrapper.class));
        }

        return config;
    }

}
