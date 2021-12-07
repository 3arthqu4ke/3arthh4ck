package me.earth.earthhack.impl.commands.packet.util;

import io.netty.util.ReferenceCounted;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BufferUtil
{
    public static void release(List<Object> objects)
    {
        for (Object o : objects)
        {
            if (o instanceof ReferenceCounted)
            {
                releaseBuffer((ReferenceCounted) o);
            }
        }
    }

    public static void release(Object...objects)
    {
        for (Object o : objects)
        {
            if (o instanceof ReferenceCounted)
            {
                releaseBuffer((ReferenceCounted) o);
            }
        }
    }

    public static void releaseFields(Object o)
    {
        Class<?> clazz = o.getClass();
        while (clazz != Object.class)
        {
            for (Field f : clazz.getDeclaredFields())
            {
                if (ReferenceCounted.class.isAssignableFrom(f.getType()))
                {
                    try
                    {
                        f.setAccessible(true);
                        ReferenceCounted buffer = (ReferenceCounted) f.get(o);
                        if (buffer != null)
                        {
                            releaseBuffer(buffer);
                        }
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static List<Object> saveReleasableFields(Object object)
    {
        List<Object> objects = new ArrayList<>(2);
        Class<?> clazz = object.getClass();
        while (clazz != Object.class)
        {
            for (Field field : clazz.getDeclaredFields())
            {
                if (ReferenceCounted.class.isAssignableFrom(field.getType()))
                {
                    try
                    {
                        field.setAccessible(true);
                        objects.add(field.get(object));
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }

        return objects;
    }

    public static void releaseBuffer(ReferenceCounted buffer)
    {
        buffer.release(buffer.refCnt());
    }

}
