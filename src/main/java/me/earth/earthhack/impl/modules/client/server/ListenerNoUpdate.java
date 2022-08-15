package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolPlayUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;

final class ListenerNoUpdate extends
        ModuleListener<ServerModule, NoMotionUpdateEvent>
{
    public ListenerNoUpdate(ServerModule module)
    {
        super(module, NoMotionUpdateEvent.class);
    }

    @Override
    public void invoke(NoMotionUpdateEvent event)
    {
        if (module.currentMode == ServerMode.Client
            || !module.sync.getValue()
            || event.isCancelled())
        {
            return;
        }

        ProtocolPlayUtil.sendVelocityAndPosition(
                module.connectionManager,
                RotationUtil.getRotationPlayer());
    }

}
