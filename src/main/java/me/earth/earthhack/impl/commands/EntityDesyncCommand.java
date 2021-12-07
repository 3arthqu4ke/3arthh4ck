package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.SendListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

public class EntityDesyncCommand extends Command implements Globals
{
    private Entity dismounted;

    public EntityDesyncCommand()
    {
        super(new String[][]{{"entitydesync"},
                             {"dismount", "remount", "delete"}});
        CommandDescriptions.register(this,
                "EntityDesync for exploit purposes.");

        Bus.EVENT_BUS.register(new EventListener<WorldClientEvent.Load>
            (WorldClientEvent.Load.class)
            {
                @Override
                public void invoke(WorldClientEvent.Load event)
                {
                    dismounted = null;
                }
            });

        Bus.EVENT_BUS.register(new EventListener<TickEvent>
            (TickEvent.class) // maybe MotionUpdateEvent?
            {
                @Override
                public void invoke(TickEvent event)
                {
                    if (mc.player != null && dismounted != null)
                    {
                        if (mc.player.isRiding())
                        {
                            dismounted = null;
                            return;
                        }

                        dismounted.setPosition(
                                mc.player.posX, mc.player.posY, mc.player.posZ);
                        mc.player.connection.sendPacket(
                                new CPacketVehicleMove(dismounted));
                    }
                }
            });

        Bus.EVENT_BUS.register(new SendListener<>(
            CPacketPlayer.Position.class, event ->
            {
                if (dismounted != null)
                {
                    event.setCancelled(true);
                }
            }));

        Bus.EVENT_BUS.register(new SendListener<>(
            CPacketPlayer.PositionRotation.class, event ->
            {
                if (dismounted != null)
                {
                    event.setCancelled(true);
                }
            }));

        /* Bus.EVENT_BUS.register(new ReceiveListener<>(
            SPacketMoveVehicle.class, event ->
            {
                ???
            })); */
    }

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command.");
            return;
        }
        
        if (args.length == 1)
        {
            if (dismounted == null)
            {
                ChatUtil.sendMessage(
                    "You are currently not desynced from any entity.");
            }
            else
            {
                ChatUtil.sendMessage("You are currently dismounted.");
            }

            return;
        }

        if (args[1].equalsIgnoreCase("dismount"))
        {
            Entity entity = mc.player.getRidingEntity();
            if (entity == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "There's no entity to dismount!");
                return;
            }

            dismounted = entity;
            mc.player.dismountRidingEntity();
            mc.world.removeEntity(entity);
            ChatUtil.sendMessage(TextColor.GREEN + "Dismounted successfully.");
        }
        else if (args[1].equalsIgnoreCase("remount"))
        {
            if (dismounted == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "There's no entity to remount!");
                return;
            }

            dismounted.isDead = false;
            mc.world.spawnEntity(dismounted);
            mc.player.startRiding(dismounted, true);
            ChatUtil.sendMessage(TextColor.GREEN + "Remounted successfully.");
        }
        else if (args[1].equalsIgnoreCase("delete"))
        {
            if (dismounted == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "There's no entity to delete!");
                return;
            }

            dismounted = null;
            ChatUtil.sendMessage(TextColor.GREEN
                    + "Deleted dismounted entity.");
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Unrecognized option, try dis/remount/delete.");
        }
    }

}
