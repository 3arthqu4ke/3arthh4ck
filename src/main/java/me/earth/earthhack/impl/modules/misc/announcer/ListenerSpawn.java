package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListenerSpawn
        extends ModuleListener<Announcer, PacketEvent.Receive<SPacketSpawnObject>>
{

    public ListenerSpawn(Announcer module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if ((event.getPacket().getType() == 60
                || event.getPacket().getType() == 91)
                && (Math.abs(event.getPacket().getSpeedX() / 8000) > 0.001
                    || Math.abs(event.getPacket().getSpeedY() / 8000) > 0.001
                    || Math.abs(event.getPacket().getSpeedZ() / 8000) > 0.001)
                && module.miss.getValue())
        {
            Managers.ENTITIES.getPlayers()
                             .stream()
                             .filter(
                                 player -> player != mc.player && !Managers.FRIENDS.contains(
                                     player)).min(
                        Comparator.comparing(
                            player -> player.getDistanceSq(event.getPacket().getX(),
                                                           event.getPacket().getY(),
                                                           event.getPacket().getZ()))).ifPresent(
                        closestPlayer -> module.arrowMap.put(
                            event.getPacket().getEntityID(), closestPlayer));

        }
    }

}
