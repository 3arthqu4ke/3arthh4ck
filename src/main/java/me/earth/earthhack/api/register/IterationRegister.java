package me.earth.earthhack.api.register;

import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link Register} backed by an {@link ArrayList}.
 * Fast Iteration and not Threadsafe.
 *
 * @param <T> the type of Object registered.
 */
// TODO: any actual evidence that ArrayList iterates so much faster???????????
public abstract class IterationRegister<T extends Nameable>
        implements Register<T>
{
    protected final List<T> registered = new ArrayList<>();

    @Override
    public void register(T object) throws AlreadyRegisteredException
    {
        T alreadyRegistered = getObject(object.getName());
        if (alreadyRegistered != null)
        {
            throw new AlreadyRegisteredException(object, alreadyRegistered);
        }

        registered.add(object);
        if (object instanceof Registrable)
        {
            ((Registrable) object).onRegister();
        }
    }

    @Override
    public void unregister(T object) throws CantUnregisterException
    {
        if (object instanceof Registrable)
        {
            ((Registrable) object).onUnRegister();
        }

        registered.remove(object);
    }

    @Override
    public T getObject(String nameIn)
    {
        String name = nameIn.toLowerCase();
        for (T t : registered)
        {
            if (t.getName().equalsIgnoreCase(name))
            {
                return t;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends T> C getByClass(Class<C> clazz)
    {
        for (T t : registered)
        {
            if (clazz == t.getClass())
            {
                return (C) t;
            }
        }

        return null;
    }

    @Override
    public Collection<T> getRegistered()
    {
        return registered;
    }

}
