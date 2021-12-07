package me.earth.earthhack.api.register;

import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

import java.util.Collection;

public interface Register<T extends Nameable>
{
    void register(T object) throws AlreadyRegisteredException;

    void unregister(T object) throws CantUnregisterException;

    T getObject(String name);

    <C extends T> C getByClass(Class<C> clazz);

    Collection<T> getRegistered();

}
