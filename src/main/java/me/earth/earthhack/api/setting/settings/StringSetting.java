package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;

public class StringSetting extends Setting<String>
{
    public StringSetting(String nameIn, String initialValue)
    {
        super(nameIn, initialValue);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(element.getAsString());
    }

    @Override
    public SettingResult fromString(String string)
    {
        setValue(string);
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String toJson()
    {
        return value == null ? "null" : value.replace("\\", "\\\\");
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return "<name>";
        }

        return "";
    }

}
