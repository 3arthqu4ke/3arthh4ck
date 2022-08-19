package me.earth.earthhack.impl.gui.visibility;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.impl.Earthhack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PageBuilder<T>
{
    /** Using a list here cause unsure about Map with List keys... idk its whatever(?) */
    private final List<Map.Entry<List<Setting<?>>, VisibilitySupplier>> suppliers;
    private final SettingContainer container;
    private final Setting<T> pageSetting;

    private Function<VisibilitySupplier, VisibilitySupplier> conversion;
    private Setting<?> position;
    private boolean injectBefore;

    /**
     * Constructs a new PageBuilder.
     * No Factory Method, People should be able to extend this.
     */
    public PageBuilder(SettingContainer container, Setting<T> setting)
    {
        this.suppliers = new LinkedList<>();
        this.pageSetting = Visibilities.requireNonNull(setting);
        this.conversion = v -> v;
        this.container = Objects.requireNonNull(container);
    }

    public PageBuilder<T> withConversion(
            Function<VisibilitySupplier, VisibilitySupplier> conversion)
    {
        this.conversion = conversion;
        return this;
    }

    /**
     * Normally the conversion should be already applied,
     * but this will apply the conversion to all VisibilitySuppliers
     * again.
     *
     * @return this PageBuilder.
     */
    public PageBuilder<T> reapplyConversion()
    {
        for (Map.Entry<List<Setting<?>>, VisibilitySupplier> entry : suppliers)
        {
            entry.setValue(conversion.apply(entry.getValue()));
        }

        return this;
    }

    public PageBuilder<T> setPagePositionBefore(String before)
    {
        return setPagePositionBefore(
                Visibilities.requireNonNull(container.getSetting(before)));
    }

    public PageBuilder<T> setPagePositionBefore(String before, Class<?> clazz)
    {
        return setPagePositionBefore(Visibilities.requireNonNull(
                                        container.getSetting(before, clazz)));
    }

    public PageBuilder<T> setPagePositionAfter(String after, Class<?> clazz)
    {
        return setPagePositionAfter(Visibilities.requireNonNull(
                                        container.getSetting(after, clazz)));
    }

    public PageBuilder<T> setPagePositionAfter(String after)
    {
        position = Visibilities.requireNonNull(container.getSetting(after));
        injectBefore = false;
        return this;
    }

    public PageBuilder<T> setPagePositionBefore(Setting<?> before)
    {
        position = Visibilities.requireNonNull(before);
        injectBefore = true;
        return this;
    }

    public PageBuilder<T> setPagePositionAfter(Setting<?> after)
    {
        position = Visibilities.requireNonNull(after);
        injectBefore = false;
        return this;
    }

    public PageBuilder<T> addVisibility(Predicate<T> visibility,
                                        Setting<?> setting)
    {
        List<Setting<?>> list = new ArrayList<>(1);
        list.add(setting);
        suppliers.add(new AbstractMap.SimpleEntry<>(list, toVis(visibility)));
        return this;
    }

    public PageBuilder<T> addPage(Predicate<T> visibility,
                                  Setting<?> start,
                                  Setting<?> end)
    {
        List<Setting<?>> settings = new ArrayList<>();
        boolean started = false;
        for (Setting<?> setting : container.getSettings())
        {
            if (setting.equals(start))
            {
                started = true;
                settings.add(setting);
            }

            if (started)
            {
                settings.add(setting);
            }

            if (setting.equals(end))
            {
                if (!started)
                {
                    // Exception would be too harsh imo
                    Earthhack.getLogger().warn("PageBuilder: found end: "
                            + end.getName()
                            + " but not start!");
                    return this;
                }

                started = false;
                settings.add(setting);
            }
        }

        if (started)
        {
            Earthhack.getLogger().warn("PageBuilder: found start: "
                    + start.getName()
                    + " but not end!");
            return this;
        }

        suppliers.add(new AbstractMap.SimpleEntry<>(settings, toVis(visibility)));
        return this;
    }

    public PageBuilder<T> addPage(Predicate<T> visibility,
                                  Setting<?>...settings)
    {
        List<Setting<?>> toArrayList = new ArrayList<>(settings.length);
        for (Setting<?> setting : settings)
        {
            if (setting != null)
            {
                toArrayList.add(setting);
            }
        }

        suppliers.add(new AbstractMap.SimpleEntry<>(toArrayList, toVis(visibility)));
        return this;
    }

    public PageBuilder<T> register(VisibilityManager manager)
    {
        for (Map.Entry<List<Setting<?>>, VisibilitySupplier> entry : suppliers)
        {
            Visibilities.register(manager, entry.getValue(), entry.getKey());
        }

        return this;
    }

    public PageBuilder<T> registerPageSetting()
    {
        if (position == null)
        {
            container.register(pageSetting);
            return this;
        }

        if (injectBefore)
        {
            container.registerBefore(pageSetting, position);
        }
        else
        {
            container.registerAfter(pageSetting, position);
        }

        return this;
    }

    public Setting<T> getPageSetting()
    {
        return pageSetting;
    }

    private VisibilitySupplier toVis(Predicate<T> predicate)
    {
        return conversion.apply(() -> predicate.test(pageSetting.getValue()));
    }

}
