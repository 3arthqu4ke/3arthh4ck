package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;

public class BooleanSetting extends Setting<Boolean>
{
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    public BooleanSetting(String nameIn, Boolean initialValue)
    {
        super(nameIn, initialValue);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(element.getAsBoolean());
    }

    @Override
    public Setting<Boolean> copy() {
        return new BooleanSetting(getName(), getInitial());
    }

    @Override
    public SettingResult fromString(String string)
    {
        if ("true".equalsIgnoreCase(string))
        {
            this.setValue(true);
            return SettingResult.SUCCESSFUL;
        }
        else if ("false".equalsIgnoreCase(string))
        {
            this.setValue(false);
            return SettingResult.SUCCESSFUL;
        }

        return new SettingResult(false, string
                + " is a bad input. Value should be \"true\" or \"false\".");
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return "<true/false>";
        }

        if (TRUE.startsWith(string.toLowerCase()))
        {
            return TRUE;
        }
        else if (FALSE.startsWith(string.toLowerCase()))
        {
            return FALSE;
        }

        return "";
    }

}
