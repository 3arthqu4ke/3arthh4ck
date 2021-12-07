package me.earth.earthhack.installer.main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryFinder
{
    private static final Map<String, URL> LIBRARIES = new HashMap<>(3);

    static
    {
        String asm_lib  = "org/ow2/asm/asm-debug-all/5.2/asm-debug-all-5.2.jar";
        URL asm_url     = toUrl("https://repo1.maven.org/maven2/"  + asm_lib);
        LIBRARIES.put(asm_lib, asm_url);

        String gson_lib = "com/google/code/gson/gson/2.8.0/gson-2.8.0.jar";
        URL gson_url    = toUrl("https://libraries.minecraft.net/" + gson_lib);
        LIBRARIES.put(gson_lib, gson_url);
    }

    public List<Library> findLibraries(MinecraftFiles files)
    {
        List<Library> result = new ArrayList<>(LIBRARIES.size());
        for (Map.Entry<String, URL> lib : LIBRARIES.entrySet())
        {
            String path = files.getLibraries() + lib.getKey();
            boolean exists = new File(path).exists();
            URL url = toUrl("file:/" + path);
            result.add(new Library(url, lib.getValue(), !exists));
        }

        return result;
    }

    private static URL toUrl(String s)
    {
        try
        {
            return new URL(s);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

}
