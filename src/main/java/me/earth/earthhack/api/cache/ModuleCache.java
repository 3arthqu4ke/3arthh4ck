package me.earth.earthhack.api.cache;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.bind.Toggle;

import java.util.function.Supplier;

/**
 * Cache/Proxy for a {@link Module}.
 *
 * The main idea behind this Cache is to call
 * {@link Register#getByClass(Class)} the least
 * amount of times possible.
 *
 * @param <T> the type of module being cached.
 */
@SuppressWarnings("unused")
public class ModuleCache<T extends Module> extends Cache<T>
{
    protected Class<T> type;

    /** Private Ctr. */
    private ModuleCache() { }

    /**
     * Constructs a ModuleCache whose supplier calls
     * {@link Register#getByClass(Class)} for the
     * given type. The given ModuleManager is Nullable.
     *
     * @param moduleManager the moduleManager to get the Module from.
     * @param type the type of the Module.
     */
    public ModuleCache(Register<Module> moduleManager, Class<T> type)
    {
        this(() ->
        {
            if (moduleManager != null && type != null)
            {
                return moduleManager.getByClass(type);
            }

            return null;
        }, type);
    }

    public ModuleCache(Supplier<T> getter, Class<T> type)
    {
        super(getter);
        this.type = type;
    }

    /**
     * Sets the ModuleManager this Cache gets its value from.
     * Note that the supplier will then call {@link Register#getByClass(Class)}.
     *
     * @param moduleManager the moduleManager.
     */
    public void setModuleManager(Register<Module> moduleManager)
    {
        this.getter = () ->
        {
            if (moduleManager != null && type != null)
            {
                return moduleManager.getByClass(type);
            }

            return null;
        };
    }

    /**
     * Calls {@link Module#enable()}, if it's present.
     *
     * @return <tt>true</tt> if present.
     */
    public boolean enable()
    {
        return computeIfPresent(Module::enable);
    }

    /**
     * Calls {@link Module#disable()}, if it's present.
     *
     * @return <tt>true</tt> if present.
     */
    public boolean disable()
    {
        return computeIfPresent(Module::disable);
    }

    /**
     * Calls {@link Module#toggle()}, if it's present.
     *
     * @return <tt>true</tt> if present.
     */
    public boolean toggle()
    {
        return computeIfPresent(Module::toggle);
    }

    /**
     * Returns <tt>true</tt>, only if the module
     * isPresent and {@link Module#isEnabled()}.
     *
     * @return <tt>false</tt> if not present or disabled.
     */
    public boolean isEnabled()
    {
        if (isPresent())
        {
            return get().isEnabled();
        }

        return false;
    }

    /**
     * @return {@link Module#getDisplayInfo()}, if present.
     */
    public String getDisplayInfo()
    {
        if (isPresent())
        {
            return get().getDisplayInfo();
        }

        return null;
    }

    /**
     * @return {@link Module#getCategory()}, if present.
     */
    public Category getCategory()
    {
        if (isPresent())
        {
            return get().getCategory();
        }

        return null;
    }

    /**
     * @return {@link Module#getData()}, if present.
     */
    public ModuleData getData()
    {
        if (isPresent())
        {
            return get().getData();
        }

        return null;
    }

    /**
     * Calls {@link Module#setData(ModuleData)}, if present.
     *
     * @return <tt>true</tt>, if present.
     */
    public boolean setData(ModuleData data)
    {
        if (isPresent())
        {
            get().setData(data);
            return true;
        }

        return false;
    }

    /**
     * @return {@link Module#getBind()}, if present.
     */
    public Bind getBind()
    {
        if (isPresent())
        {
            return get().getBind();
        }

        return null;
    }

    /**
     * @return {@link Module#getBindMode()}, if present.
     */
    public Toggle getBindMode()
    {
        if (isPresent())
        {
            return get().getBindMode();
        }

        return null;
    }

    /**
     * Constructs a ModuleCache whose Supplier
     * calls {@link Register#getObject(String)}.
     *
     * @param name the name for the Module.
     * @param manager the Register to get the Module from.
     * @return a ModuleCache for Modules of the given Name.
     */
    public static ModuleCache<Module> forName(String name,
                                              Register<Module> manager)
    {
        NameCache cache = new NameCache(name);
        cache.setModuleManager(manager);
        return cache;
    }

    private static final class NameCache extends ModuleCache<Module>
    {
        private final String name;

        public NameCache(String name)
        {
            this.name = name;
            this.type = Module.class;
        }

        @Override
        public void setModuleManager(Register<Module> moduleManager)
        {
            this.getter = () ->
            {
                if (moduleManager != null)
                {
                    return moduleManager.getObject(name);
                }

                return null;
            };
        }
    }

}
