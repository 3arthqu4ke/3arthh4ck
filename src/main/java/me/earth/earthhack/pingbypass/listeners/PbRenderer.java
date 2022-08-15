package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CRenderPacket;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.HashMap;
import java.util.Map;

public class PbRenderer extends SubscriberImpl implements Globals {
    private final Map<AxisAlignedBB, Render> renders = new HashMap<>();
    private final MutableBB bb = new MutableBB();

    public PbRenderer() {
        this.listeners.add(new LambdaListener<>(
            TickEvent.class, event -> renders.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().getTimeStamp() >= 250)));
        this.listeners.add(new LambdaListener<>(Render3DEvent.class, e -> {
            for (Render render : renders.values()) {
                AxisAlignedBB b = render.packet.getBb();
                bb.setBB(
                    b.minX - mc.getRenderManager().viewerPosX,
                    b.minY - mc.getRenderManager().viewerPosY,
                    b.minZ - mc.getRenderManager().viewerPosZ,
                    b.maxX - mc.getRenderManager().viewerPosX,
                    b.maxY - mc.getRenderManager().viewerPosY,
                    b.maxZ - mc.getRenderManager().viewerPosZ);

                BBRender.renderBox(bb, render.packet.getColor(), render.packet.getOutLine(), 1.5f);
            }
        }));
    }

    public void addRender(S2CRenderPacket packet) {
        renders.put(packet.getBb(), new Render(packet));
    }

    private static final class Render extends TimeStamp {
        private final S2CRenderPacket packet;

        private Render(S2CRenderPacket packet) {
            this.packet = packet;
        }
    }

}
