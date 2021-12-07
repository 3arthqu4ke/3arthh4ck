package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This ConfigHelper handles all Configs that are currently active.
 * The Methods: {@link CurrentConfig#save(String)},
 * {@link CurrentConfig#load(String)}, {@link CurrentConfig#refresh(String)}
 * and {@link CurrentConfig#delete(String)} will throw an
 * {@link UnsupportedOperationException}.
 */
@SuppressWarnings("rawtypes")
public class CurrentConfig implements ConfigHelper
{
    private static final CurrentConfig INSTANCE = new CurrentConfig();
    private static final String PATH = "earthhack/Configs.json";

    private final Map<ConfigHelper, String> configs = new HashMap<>();
    private final Map<String, String> additional = new HashMap<>();

    /** Private Ctr since this is a Singleton */
    private CurrentConfig() { }

    /** @return the Singleton Instance for this class. */
    public static CurrentConfig getInstance()
    {
        return INSTANCE;
    }

    public void set(ConfigHelper helper, String config)
    {
        configs.put(helper, config);
    }

    public String get(ConfigHelper helper)
    {
        return configs.get(helper);
    }

    public void set(String additional, String config)
    {
        this.additional.put(additional, config);
    }

    public String get(String additional)
    {
        return this.additional.get(additional);
    }

    @Override
    public void save() throws IOException
    {
        Path file = Paths.get(PATH);
        if (!Files.exists(file))
        {
            Files.createFile(file);
        }

        JsonObject object = new JsonObject();
        for (Map.Entry<ConfigHelper, String> entry : configs.entrySet())
        {
            object.add(entry.getKey().getName(),
                       Jsonable.parse(entry.getValue()));
        }

        for (Map.Entry<String, String> entry : additional.entrySet())
        {
            object.add(entry.getKey(), Jsonable.parse(entry.getValue()));
        }

        JsonPathWriter.write(file, object);
    }

    @Override
    public void refresh() throws IOException
    {
        try (InputStream stream = Files.newInputStream(Paths.get(PATH)))
        {
            JsonObject object = Jsonable.PARSER
                                        .parse(new InputStreamReader(stream))
                                        .getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                ConfigHelper helper = Managers.CONFIG.getObject(entry.getKey());
                if (helper != null)
                {
                    set(helper, entry.getValue().getAsString());
                }
                else
                {
                    additional.put(entry.getKey(),
                                   entry.getValue().getAsString());
                }
            }
        }
    }

    /** Will throw an {@link UnsupportedOperationException}. */
    @Override
    public void save(String name) throws IOException
    {
        throw new UnsupportedOperationException(
                "CurrentConfig doesn't support multiple configs.");
    }

    /** Will throw an {@link UnsupportedOperationException}. */
    @Override
    public void load(String name)
    {
        throw new UnsupportedOperationException(
                "CurrentConfig doesn't support multiple configs.");
    }

    /** Will throw an {@link UnsupportedOperationException}. */
    @Override
    public void refresh(String name) throws IOException
    {
        throw new UnsupportedOperationException(
                "CurrentConfig doesn't support multiple configs.");
    }

    /** Will throw an {@link UnsupportedOperationException}. */
    @Override
    public void delete(String name) throws ConfigDeleteException
    {
        throw new ConfigDeleteException(
                "CurrentConfig doesn't support multiple configs.");
    }

    @Override
    public Collection<? extends Nameable> getConfigs()
    {
        return configs.keySet();
    }

    @Override
    public String getName()
    {
        return "current";
    }

}
