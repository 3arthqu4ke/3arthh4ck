package me.earth.earthhack.impl.util.misc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLUtil
{
    public static URL toUrl(String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static URL toUrl(URI uri)
    {
        try
        {
            return uri.toURL();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

}
