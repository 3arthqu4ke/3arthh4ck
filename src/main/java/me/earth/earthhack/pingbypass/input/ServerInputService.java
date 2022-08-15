package me.earth.earthhack.pingbypass.input;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;

public class ServerInputService extends SubscriberImpl {
    public ServerInputService() {
        this.listeners.add(new LambdaListener<>(MouseEvent.class, e ->
            Mouse.setButtonDown(e.getButton(), e.getState())));
    }

}
