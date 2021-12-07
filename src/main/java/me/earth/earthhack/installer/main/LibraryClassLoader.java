package me.earth.earthhack.installer.main;

import me.earth.earthhack.impl.util.misc.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class LibraryClassLoader extends URLClassLoader
{
    public LibraryClassLoader(ClassLoader parent, URL...urls)
    {
        super(urls, parent);
    }

    public void installLibrary(Library library) throws Exception
    {
        if (library.needsDownload())
        {
            //noinspection ResultOfMethodCallIgnored
            new File(library.getUrl().getFile()).getParentFile().mkdirs();

            try (ReadableByteChannel rbc =
                        Channels.newChannel(library.getWeb().openStream());
                FileOutputStream fos =
                        new FileOutputStream(library.getUrl().getFile()))
            {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
        }

        this.addURL(library.getUrl());
    }

    public Class<?> findClass_public(String name) throws ClassNotFoundException
    {
        return findClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        if (name.startsWith("java.")
            || name.startsWith("javax.")
            || name.startsWith("sun.")
            || name.startsWith("me.earth.earthhack.installer.main")
            || name.startsWith("jdk."))
        {
            return super.loadClass(name, resolve);
        }

        Class<?> alreadyLoaded = findLoadedClass(name);
        if (alreadyLoaded != null)
        {
            return alreadyLoaded;
        }

        try (InputStream is = getResourceAsStream(
                name.replaceAll("\\.", "/") + ".class"))
        {
            if (is == null)
            {
                throw new ClassNotFoundException("Could not find " + name);
            }

            byte[] bytes = StreamUtil.toByteArray(is);
            Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
            if (resolve)
            {
                resolveClass(clazz);
            }

            return clazz;
        }
        catch (IOException e)
        {
            throw new ClassNotFoundException("Could not load " + name, e);
        }
    }

}
