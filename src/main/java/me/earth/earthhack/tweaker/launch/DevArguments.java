package me.earth.earthhack.tweaker.launch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DevArguments implements ArgumentManager
{
    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");
    private static final DevArguments INSTANCE = new DevArguments();
    private static final String PATH = "earthhack/dev.json";

    private final Map<String, Argument<?>> arguments;

    private DevArguments()
    {
        this.arguments = new ConcurrentHashMap<>();
    }

    public static DevArguments getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void loadArguments()
    {
        Path path = Paths.get(PATH);
        if (!Files.exists(path))
        {
            return;
        }

        try (InputStream stream = Files.newInputStream(path))
        {
            JsonObject object = Jsonable.PARSER
                                        .parse(new InputStreamReader(stream))
                                        .getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                Argument<?> argument = getArgument(entry.getKey());
                if (argument == null)
                {
                    LOGGER.warn(
                        "Unknown DevArgument: " + entry.getKey() + "!");
                    continue;
                }

                argument.fromJson(entry.getValue());
                LOGGER.info("Dev-Argument: "
                    + entry.getKey() + " : " + argument.getValue());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void addArgument(String name, Argument<?> argument)
    {
        if (arguments.containsKey(name))
        {
            throw new IllegalStateException(
                    "Argument with name: " + name + " already exists!");
        }

        arguments.put(name, argument);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Argument<T> getArgument(String name)
    {
        return (Argument<T>) arguments.get(name);
    }

}
