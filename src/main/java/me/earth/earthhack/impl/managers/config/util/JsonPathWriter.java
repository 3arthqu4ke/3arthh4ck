package me.earth.earthhack.impl.managers.config.util;

import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonPathWriter
{
    public static void write(Path path, JsonObject object) throws IOException
    {
        String json = Jsonable.GSON.toJson(object);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(path))))
        {
            writer.write(json);
        }
    }

}
