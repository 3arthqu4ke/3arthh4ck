package me.earth.earthhack.impl.util.misc.collections;

import java.util.Collection;
import java.util.Stack;

/**
 * An implementation of {@link Stack}, that, instead of throwing
 * Exceptions, just returns <tt>null</tt> if the Stack is empty.
 *
 * @param <E> type of Object contained in this stack.
 */
public class ConvenientStack<E> extends Stack<E>
{
    /** Default Ctr. */
    public ConvenientStack() { }

    /**
     * @param collection all objects contained will be added to this Stack.
     */
    public ConvenientStack(Collection<E> collection)
    {
        addAll(collection);
    }

    @Override
    public synchronized E pop()
    {
        if (empty())
        {
            return null;
        }

        return super.pop();
    }

    @Override
    public synchronized E peek()
    {
        if (empty())
        {
            return null;
        }

        return super.peek();
    }

}
