package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;

public interface ISender
{
    void send(byte[] packet) throws IOException;

}
