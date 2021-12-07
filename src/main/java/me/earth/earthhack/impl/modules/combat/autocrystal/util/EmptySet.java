package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("NullableProblems")
public class EmptySet<T> implements Set<T>
{
    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean contains(Object o)
    {
        return false;
    }

    @Override
    public Iterator<T> iterator()
    {
        return Collections.emptyIterator();
    }

    @Override
    public Object[] toArray()
    {
        return new Object[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] a)
    {
        return (T1[]) new Object[0];
    }

    @Override
    public boolean add(T t)
    {
        return false;
    }

    @Override
    public boolean remove(Object o)
    {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public void clear() { }
}
