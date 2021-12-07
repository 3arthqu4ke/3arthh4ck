package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

@FunctionalInterface
public interface IORunnable
{
    void run() throws IOException;

}
