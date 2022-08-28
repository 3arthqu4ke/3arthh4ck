package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.interfaces.Nameable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: perhaps not just nameables?
public class ListSetting<M extends Nameable> extends Setting<M> {
    private final List<M> values;
    private final String concatenated;

    public ListSetting(String nameIn, M initialValue, List<M> values) {
        super(nameIn, initialValue);
        this.values = values;
        this.concatenated = concatenateInputs();
    }

    @Override
    public void fromJson(JsonElement element) {
        String name = element.getAsString();
        for (M nameable : values) {
            if (nameable.getName().equalsIgnoreCase(name))
                setValue(nameable);
        }
    }

    @Override
    public Setting<M> copy() {
        return new ListSetting<>(getName(), getInitial(), new ArrayList<>(
            Collections.singletonList(getInitial())));
    }

    @Override
    public SettingResult fromString(String string) {
        for (Nameable nameable : values) {
            if (nameable.getName().equalsIgnoreCase(string)) {
                return SettingResult.SUCCESSFUL;
            }
        }
        return new SettingResult(false,
                                 "No value found with name " + string + ".");
    }

    @Override
    public String getInputs(String string) {
        if (string == null || string.length() == 0) {
            return concatenated;
        }

        for (Nameable nameable : values) {
            if (nameable.getName().startsWith(string))
                return nameable.getName();
        }

        return "";
    }

    private String concatenateInputs() {
        StringBuilder builder = new StringBuilder("<");
        for (Nameable nameable : values) {
            builder.append(nameable.getName()).append(", ");
        }

        if (builder.length() < 2) {
            builder.append(", ");
        }
        builder.replace(builder.length() - 2, builder.length(), ">");
        return builder.toString();
    }

    public List<M> getValues() {
        return values;
    }

}
