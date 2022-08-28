package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.helpers.addable.setting.Removable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class RegisteringModule<I, E extends Setting<I> & Removable>
        extends AddableModule
{
    protected final Function<Setting<?>, String> settingDescription;
    protected final Set<E> added = new LinkedHashSet<>();
    protected final Function<String, E> create;

    public RegisteringModule(String name,
                             Category category,
                             String command,
                             String descriptor,
                             Function<String, E> create,
                             Function<Setting<?>, String> settingDescription)
    {
        super(name, category, command, descriptor);
        this.create = create;
        this.settingDescription = settingDescription;
    }

    @Override
    public void add(String string)
    {
        addSetting(string);
    }

    @Override
    public void del(String string)
    {
        super.del(string);
        Setting<?> setting = this.getSetting(string);
        if (setting != null)
        {
            unregister(setting);
        }
    }

    @Override
    public Setting<?> getSettingConfig(String name)
    {
        Setting<?> setting = super.getSetting(name);
        if (setting == null)
        {
            Setting<?> generated = addSetting(name);
            if (generated != null)
            {
                ModuleData<?> data = this.getData();
                if (data != null)
                {
                    data.settingDescriptions().put(generated,
                            settingDescription.apply(generated));
                }

                GeneratedSettings.add(this, generated);
            }

            return generated;
        }

        return setting;
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public Setting<?> unregister(Setting<?> setting)
    {
        added.remove(setting);
        strings.remove(formatString(setting.getName()));
        return super.unregister(setting);
    }

    protected E addSetting(String string)
    {
        E newSetting = create.apply(string);
        if (added.add(newSetting))
        {
            ModuleData<?> data = this.getData();
            if (data != null)
            {
                data.settingDescriptions().put(newSetting,
                        settingDescription.apply(newSetting));
            }

            register(newSetting);
            super.add(string);
            return newSetting;
        }

        return null;
    }

}
