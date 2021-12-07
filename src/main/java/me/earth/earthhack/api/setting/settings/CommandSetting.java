package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.event.SettingResult;

import java.util.function.Consumer;

/**
 * A setting with a practically immutable
 * Value, like "Add block...". Can be used to
 * run commands or other stuff in the gui.
 * When a value is entered {@link CommandSetting#onInput(String)}
 * will be called. And the value won't be set.
 */
public class CommandSetting extends StringSetting
{
    private final Consumer<String> inputReader;

    public CommandSetting(String nameIn, Consumer<String> inputReader)
    {
        super(nameIn, "<...>");
        this.inputReader = inputReader;
    }

    public void onInput(String input)
    {
        inputReader.accept(input);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        /* nothing */
    }

    @Override
    public SettingResult fromString(String string)
    {
        onInput(string);
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public void setValue(String value, boolean withEvent)
    {
        onInput(value);
    }

}
