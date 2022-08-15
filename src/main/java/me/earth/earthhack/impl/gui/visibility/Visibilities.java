package me.earth.earthhack.impl.gui.visibility;

import me.earth.earthhack.api.setting.Setting;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Utility class for Visibilities.
 * Also contains an instance of the {@link VisibilityManager}.
 */
public class Visibilities
{
    /** An Instance of a {@link VisibilityManager}. */
    public static final VisibilityManager VISIBILITY_MANAGER =
            new VisibilityManager();

    /**
     * Like {@link Objects#requireNonNull(Object, Supplier)}:
     * Throws a NullPointerException if the given Setting
     * is <tt>null</tt> and returns the given Setting.
     *
     * @param setting the setting to check.
     * @param <T> the type of the Settings values
     * @param <S> the type of the Setting.
     * @return the setting.
     */
    public static <T, S extends Setting<T>> S requireNonNull(S setting)
    {
        if (setting == null)
            throw new NullPointerException();
        return setting;
    }

    /**
     * Registers all Settings returned by the given
     * Iterable on the given VisibilityManger for the
     * given VisibilitySupplier.
     *
     * @param manager the manager to register the settings with.
     * @param supplier the VisibilitySupplier for the settings.
     * @param settings the settings to register.
     */
    public static void register(VisibilityManager manager,
                                VisibilitySupplier supplier,
                                Iterable<? extends Setting<?>> settings)
    {
        for (Setting<?> setting : settings)
        {
            manager.registerVisibility(setting, supplier);
        }
    }

    public static VisibilitySupplier andComposer(VisibilitySupplier supplier)
    {
        return withComposer(supplier, v -> v.isVisible()
                                                && supplier.isVisible());
    }

    public static VisibilitySupplier orComposer(VisibilitySupplier supplier)
    {
        return withComposer(supplier, v -> v.isVisible()
                                                || supplier.isVisible());
    }

    public static VisibilitySupplier withComposer(
                      VisibilitySupplier supplier,
                      Function<VisibilitySupplier, Boolean> composer)
    {
        return new VisibilitySupplier()
        {
            @Override
            public boolean isVisible()
            {
                return supplier.isVisible();
            }

            @Override
            public VisibilitySupplier compose(VisibilitySupplier other)
            {
                return () -> composer.apply(other);
            }
        };
    }

}
