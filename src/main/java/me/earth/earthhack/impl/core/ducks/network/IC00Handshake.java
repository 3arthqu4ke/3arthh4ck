package me.earth.earthhack.impl.core.ducks.network;

/**
 * Accessor for
 * {@link net.minecraft.network.handshake.client.C00Handshake}.
 */
public interface IC00Handshake
{
    /**
     * Cancels adding of FML marker.
     *
     * @param cancel if you want no FML marker.
     */
    void cancelFML(boolean cancel);

    /**
     * Sets the packets ip.
     *
     * @param ip the ip.
     */
    void setIP(String ip);

    /**
     * Sets the packets port.
     *
     * @param port the port.
     */
    void setPort(int port);

    String getIp();

    int getPort();

}
