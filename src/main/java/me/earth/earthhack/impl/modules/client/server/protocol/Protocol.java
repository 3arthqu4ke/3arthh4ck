package me.earth.earthhack.impl.modules.client.server.protocol;

public final class Protocol
{
    /** Updates a connections name. */
    public static final int NAME      = 0x00;
    /** Invokes a command at the client. server -> client only. */
    public static final int COMMAND   = 0x01;
    /** Sends a message to the client. */
    public static final int MESSAGE   = 0x02;
    /** Sends a packet to the client. */
    public static final int PACKET    = 0x03;
    /** Sends a global message to all clients connected to the host. */
    public static final int GLOBAL    = 0x04;
    /** Sends a private message. To one client connected to the host. */
    public static final int PRIVATE   = 0x05;
    /** Informs a client about an exception. */
    public static final int EXCEPTION = 0x06;
    /** Custom bytes. */
    public static final int CUSTOM    = 0x07;
    /** Sends a client the list of clients connected to the server. */
    public static final int LIST      = 0x08;
    /** For Syncing: sends the current position. */
    public static final int POSITION  = 0x09;
    /** For Syncing: sends the current velocity. */
    public static final int VELOCITY  = 0x0a;
    /** For Syncing: sends when the player eats. */
    public static final int EATING    = 0x0b;

    private Protocol() { throw new AssertionError(); }

    public static int[] ids()
    {
        int[] result = new int[9];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = i;
        }

        return result;
    }

}