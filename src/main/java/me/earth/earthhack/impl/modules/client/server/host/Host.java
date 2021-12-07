package me.earth.earthhack.impl.modules.client.server.host;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IHost;
import me.earth.earthhack.impl.modules.client.server.api.IShutDownHandler;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class Host implements SafeRunnable, Globals, IHost
{
    private final IConnectionManager manager;
    private final ExecutorService service;
    private final IShutDownHandler module;
    private final ServerSocket socket;
    private final boolean receive;
    private Future<?> future;

    private Host(IConnectionManager connectionManager,
                 ExecutorService service,
                 IShutDownHandler module,
                 int port,
                 boolean receive)
            throws IOException
    {
        this.socket  = new ServerSocket(port);
        this.service = service;
        this.manager = connectionManager;
        this.module  = module;
        this.receive = receive;
    }

    @Override
    public void runSafely() throws Throwable
    {
        while (!future.isCancelled())
        {
            Socket client = socket.accept();
            Connection connection = new Connection(manager, client);
            if (!manager.accept(connection))
            {
                client.close();
            }
            else if (receive)
            {
                service.submit(connection);
            }
        }
    }

    @Override
    public void handle(Throwable t)
    {
        module.disable(t.getMessage());
    }

    @Override
    public int getPort()
    {
        return socket.getLocalPort();
    }

    @Override
    public IConnectionManager getConnectionManager()
    {
        return manager;
    }

    @Override
    public void close()
    {
        if (future != null)
        {
            future.cancel(true);
        }

        if (isOpen())
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        manager.getConnections().forEach(IConnection::close);
        manager.getConnections().clear();
    }

    @Override
    public boolean isOpen()
    {
        return !socket.isClosed();
    }

    public void setFuture(Future<?> future)
    {
        this.future = future;
    }

    public static Host createAndStart(ExecutorService service,
                                      IConnectionManager manager,
                                      IShutDownHandler module,
                                      int port,
                                      boolean receive)
            throws IOException
    {
        Host host = new Host(manager, service, module, port, receive);
        host.setFuture(service.submit(host));
        return host;
    }

}
