package me.earth.earthhack.impl.modules.client.pingbypass.serializer;

/**
 * Turns an object into a CPacketChatMessage
 * which then gets turned into a command by the
 * PingBypass.
 *
 * @param <T> type of object being serialized.
 */
public interface Serializer<T>
{
    /**
     * Creates a CPacketChatMessage and
     * sends it to the PingBypass Proxy.
     *
     * @param object the object to send.
     */
    void serializeAndSend(T object);

}
