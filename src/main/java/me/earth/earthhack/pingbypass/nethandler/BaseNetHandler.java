package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseNetHandler implements ITickable {
    private static final Logger LOGGER = LogManager.getLogger(BaseNetHandler.class);
    protected final StopWatch timer = new StopWatch().reset();
    protected final NetworkManager networkManager;
    private final long timeout;

    public BaseNetHandler(NetworkManager networkManager)
    {
        this(networkManager, 30_000);
    }

    public BaseNetHandler(NetworkManager networkManager, long timeout)
    {
        this.networkManager = networkManager;
        this.timeout = timeout;
    }

    @Override
    public void update()
    {
        if (timer.passed(timeout))
        {
            this.disconnect(new TextComponentString("Timed out!"));
        }
    }

    @SuppressWarnings("unchecked")
    public void disconnect(ITextComponent reason)
    {
        try
        {
            LOGGER.info("Disconnecting {}: {}",
                        this.getConnectionInfo(), reason.getUnformattedText());
            if (this instanceof LoginHandler) {
                this.networkManager.sendPacket(
                    new net.minecraft.network.login.server.SPacketDisconnect(reason));
                networkManager.closeChannel(reason);
            } else {
                this.networkManager.sendPacket(
                    new SPacketDisconnect(reason),
                    o -> networkManager.closeChannel(reason));
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error whilst disconnecting player", exception);
        }
    }

    public String getConnectionInfo()
    {
        return "";
    }

}
