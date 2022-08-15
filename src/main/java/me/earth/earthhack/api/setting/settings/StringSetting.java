package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;

public class StringSetting extends Setting<String>
{
    private boolean isPassword;

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
    public Setting<String> copy() {
        return new StringSetting(getName(), getInitial());
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

    public boolean isPassword()
    {
        return isPassword;
    }

    public void setPassword(boolean password)
    {
        isPassword = password;
    }

    public String censor()
    {
        return censor(getValue());
    }

    public static String censor(String value)
    {
        if (value == null || value.isEmpty())
        {
            return "";
        }

        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            builder.append("*");
        }

        return builder.toString();
    }

}
