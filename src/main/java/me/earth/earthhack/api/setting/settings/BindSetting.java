package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.input.Keyboard;

public class BindSetting extends Setting<Bind>
{
    public BindSetting(String name)
    {
        this(name, Bind.none());
    }

    public BindSetting(String name, Bind initialValue)
    {
        super(name, initialValue);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        this.fromString(element.getAsString());
    }

    @Override
    public Setting<Bind> copy() {
        return new BindSetting(getName(), getInitial());
    }

    @Override
    public SettingResult fromString(String string)
    {
        if (PingBypass.isServer()) {
            return new SettingResult(false, "No Binds on PingBypass!");
        }

        if ("none".equalsIgnoreCase(string))
        {
            this.value = Bind.none();
        }
        else
        {
            this.setValue(Bind.fromString(string));
        }

        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String getInputs(String string)
    {
        if (PingBypass.isServer()) {
            return "<No Binds on PingBypass!>";
        }

        if (string == null || string.isEmpty())
        {
            return "<key>";
        }

        if ("none".startsWith(string.toLowerCase()))
        {
            return "NONE";
        }
        else
        {
            for (int i = 0; i < Keyboard.getKeyboardSize(); i++)
            {
                String keyName = Keyboard.getKeyName(i);
                if (keyName != null
                        && keyName.toLowerCase().startsWith(
                                                        string.toLowerCase()))
                {
                    return keyName;
                }
            }
        }

        return "";
    }

    public void setKey(int key)
    {
        this.value = Bind.fromKey(key);
    }

}
