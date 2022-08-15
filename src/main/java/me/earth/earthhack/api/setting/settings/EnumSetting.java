package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.EnumHelper;

public class EnumSetting<E extends Enum<E>> extends Setting<E>
{
    private final String concatenated;

    public EnumSetting(String nameIn, E initialValue)
    {
        super(nameIn, initialValue);
        concatenated = concatenateInputs();
    }

    @Override
    public void fromJson(JsonElement element)
    {
        fromString(element.getAsString());
    }

    @Override
    public Setting<E> copy() {
        return new EnumSetting<>(getName(), getInitial());
    }

    @Override
    @SuppressWarnings("unchecked")
    public SettingResult fromString(String string)
    {
        Enum<?> entry = EnumHelper.fromString(this.value, string);
        this.setValue((E) entry);
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return concatenated;
        }

        Enum<?> entry = EnumHelper.getEnumStartingWith(string,
                                                initial.getDeclaringClass());

        return entry == null ? "" : entry.toString();
    }

    private String concatenateInputs()
    {
        StringBuilder builder = new StringBuilder("<");
        Class<? extends Enum<?>> clazz = this.initial.getDeclaringClass();
        for (Enum<?> entry : clazz.getEnumConstants())
        {
            builder.append(entry.name()).append(", ");
        }

        builder.replace(builder.length() - 2, builder.length(), ">");
        return builder.toString();
    }

}
