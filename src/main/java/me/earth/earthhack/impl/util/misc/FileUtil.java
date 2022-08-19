package me.earth.earthhack.impl.util.misc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class FileUtil
{
    @SuppressWarnings("UnusedReturnValue")
    public static Path getDirectory(Path parent, String...paths)
    {
        if (paths.length < 1)
        {
            return parent;
        }

        Path dir = lookupPath(parent, paths);
        createDirectory(dir);
        return dir;
    }

    public static Path lookupPath(Path root, String...paths)
    {
        return Paths.get(root.toString(), paths);
    }

    public static boolean createDirectory(File file)
    {
        boolean created = true;
        if (!file.exists())
        {
            created = file.mkdir();
        }

        return created;
    }

    public static void createDirectory(Path dir)
    {
        try
        {
            if (!Files.isDirectory(dir))
            {
                if (Files.exists(dir))
                {
                    Files.delete(dir);
                }

                Files.createDirectories(dir);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String file,
                                        boolean write,
                                        Iterable<String> data)
    {
        try
        {
            Path path = Paths.get(file);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            if (write)
            {
                writeFile(file, data);
            }

            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public static void writeFile(String file, Iterable<String> data)
    {
        Path path = Paths.get(file);

        try
        {
            Files.write(path, data, StandardCharsets.UTF_8, Files.exists(path)
                    ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Calls {@link Paths#get(String, String...)}, and creates
     * it if {@link Files#exists(Path, LinkOption...)} returns
     * false.
     *
     * @param name the name of the path to create.
     * @param more additional strings to be joined to form the path string
     * @return the path for the args.
     * @throws IOException if invalid String or cant create path.
     */
    public static Path getPath(String name, String...more) throws IOException
    {
        Path path = Paths.get(name, more);
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }

        return path;
    }

    public static void openWebLink(URI url) throws Throwable
    {
        Class<?> clazz = Class.forName("java.awt.Desktop");
        Object object = clazz.getMethod("getDesktop").invoke(null);
        clazz.getMethod("browse", URI.class).invoke(object, url);
    }

}
