package me.earth.earthhack.impl.util.misc.collections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Util for Collections.
 */
public class CollectionUtil
{
    /**
     * Empties the queue and runs every Runnable that is != null.
     *
     * @param runnables the runnables to run.
     */
    public static void emptyQueue(Queue<Runnable> runnables)
    {
        emptyQueue(runnables, Runnable::run);
    }

    /**
     * Empties the given Queue, by polling all elements until its empty.
     * For every polled element (!= null) the given Consumer will be called.
     *
     * @param queue the queue to empty.
     * @param onPoll called for every polled element that is != null.
     * @param <T> the type of elements,
     */
    public static <T> void emptyQueue(Queue<T> queue, Consumer<T> onPoll)
    {
        while (!queue.isEmpty())
        {
            T polled = queue.poll();
            if (polled != null)
            {
                onPoll.accept(polled);
            }
        }
    }

    /**
     * Splits the list into multiple ArrayLists.
     * After an element tested true for a given predicate
     * it will be added to the split list and not be tested again.
     * The returned list will have the size of the given predicates + 1,
     * the last of the lists will consist of all elements that didn't
     * test true for any of the given predicates.
     *
     * @param list the list to split.
     * @param predicates the predicates to apply to the list.
     * @param <T> the type of the list
     * @return a list of split lists
     */
    @SafeVarargs
    public static <T> List<List<T>> split(List<T> list,
                                          Predicate<T>...predicates)
    {
        List<List<T>> result = new ArrayList<>(predicates.length + 1);

        List<T> current = new ArrayList<>(list);
        List<T> next    = new ArrayList<>();
        for (Predicate<T> p : predicates)
        {
            Iterator<T> it = current.iterator();
            while (it.hasNext())
            {
                T t = it.next();
                if (p.test(t))
                {
                    next.add(t);
                    it.remove();
                }
            }

            result.add(next);
            next = new ArrayList<>();
        }

        result.add(current);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends T> C getByClass(Class<C> clazz,
                                                 Collection<T> collection)
    {
        for (T t : collection)
        {
            if (clazz.isInstance(t))
            {
                return (C) t;
            }
        }

        return null;
    }

    public static <T, R> List<T> convert(R[] array, Function<R, T> function)
    {
        List<T> result = new ArrayList<>(array.length);
        for (int i = 0; i < array.length; i++)
        {
            result.add(i, function.apply(array[i]));
        }

        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
        sortByValue(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static <T> T getLast(Collection<T> iterable)
    {
        T last = null;
        for (T t : iterable)
        {
            last = t;
        }

        return last;
    }

}
