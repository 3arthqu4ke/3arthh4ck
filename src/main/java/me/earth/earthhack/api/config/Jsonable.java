package me.earth.earthhack.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Interface for Objects that can be serialized using Json.
 */
public interface Jsonable
{
    /** A {@link JsonParser}. */
    JsonParser PARSER = new JsonParser();
    Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    /**
     * Sets values of this object from the
     * given JsonElement.
     *
     * @param element the element.
     */
    void fromJson(JsonElement element);

    /**
     * @return the value of this object as a parsable String.
     */
    String toJson();

    static JsonElement parse(String string)
    {
        return parse(string, true);
    }

    static JsonElement parse(String string, boolean addQuotation)
    {
        JsonElement element = null;

        try(JsonReader reader = new JsonReader(
                new StringReader(addQuotation ? "\"" + string + "\"" : string)))
        {
            reader.setLenient(true);
            element = PARSER.parse(reader);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return element;
    }

}
