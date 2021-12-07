package me.earth.earthhack.api.register;

import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link Register} backed by a {@link ConcurrentHashMap}.
 *
 * @param <T> the of object to register.
 */
public abstract class AbstractRegister<T extends Nameable>
        implements Register<T>
{
    protected final Map<String, T> registered;

    @SuppressWarnings("unused")
    public AbstractRegister()
    {
        this(new ConcurrentHashMap<>());
    }

    public AbstractRegister(Map<String, T> map)
    {
        registered = map;
    }

    @Override
    public void register(T object) throws AlreadyRegisteredException
    {
        T alreadyRegistered = getObject(object.getName());
        if (alreadyRegistered != null)
        {
            throw new AlreadyRegisteredException(object, alreadyRegistered);
        }

        if (object instanceof Registrable)
        {
            ((Registrable) object).onRegister();
        }

        registered.put(object.getName().toLowerCase(), object);
    }

    @Override
    public void unregister(T object) throws CantUnregisterException
    {
        if (object instanceof Registrable)
        {
            ((Registrable) object).onUnRegister();
        }

        for (Map.Entry<String, T> entry : registered.entrySet())
        {
            if (object.equals(entry.getValue()))
            {
                registered.remove(entry.getKey());
            }
        }
    }

    @Override
    public T getObject(String name)
    {
        return registered.get(name.toLowerCase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends T> C getByClass(Class<C> clazz)
    {
        for (Map.Entry<String, T> entry : registered.entrySet())
        {
            if (clazz.isInstance(entry.getValue()))
            {
                return (C) entry.getValue();
            }
        }

        return null;
    }

    @Override
    public Collection<T> getRegistered()
    {
        return registered.values();
    }

}
