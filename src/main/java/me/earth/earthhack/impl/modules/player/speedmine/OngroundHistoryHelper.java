package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayDeque;
import java.util.Iterator;

public class OngroundHistoryHelper extends SubscriberImpl
    implements Iterable<Boolean> {
    private final ArrayDeque<Boolean> history = new ArrayDeque<>();

    public OngroundHistoryHelper() {
        this.listeners.add(new LambdaListener<>(MotionUpdateEvent.class, e -> {
            if (e.getStage() == Stage.POST) {
                history.addFirst(Managers.POSITION.isOnGround());
                while (history.size() > 200) {
                    history.removeLast();
                }
            }
        }));
    }

    public Iterator<Boolean> iterator() {
        return history.iterator();
    }

}
