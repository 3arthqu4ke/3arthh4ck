package me.earth.earthhack.impl.commands.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.packet.arguments.AdvancementArgument;
import me.earth.earthhack.impl.commands.packet.arguments.AdvancementProgressArgument;
import me.earth.earthhack.impl.commands.packet.arguments.AttributeArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BlockArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BlockPosArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BooleanArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BossInfoArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ByteArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ChunkArgument;
import me.earth.earthhack.impl.commands.packet.arguments.CollectionArgument;
import me.earth.earthhack.impl.commands.packet.arguments.CombatTrackerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.DoubleArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityDataMangerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityLivingBaseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityPaintingArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityPlayerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityXPOrbArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EnumArgument;
import me.earth.earthhack.impl.commands.packet.arguments.FloatArgument;
import me.earth.earthhack.impl.commands.packet.arguments.GameProfileArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IntArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ItemArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ItemStackArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IterableArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ListArgument;
import me.earth.earthhack.impl.commands.packet.arguments.LongArgument;
import me.earth.earthhack.impl.commands.packet.arguments.MapArgument;
import me.earth.earthhack.impl.commands.packet.arguments.MapDecorationArgument;
import me.earth.earthhack.impl.commands.packet.arguments.NBTTagCompoundArgument;
import me.earth.earthhack.impl.commands.packet.arguments.NonNullListArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PacketBufferArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PlayerCapabilitiesArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PotionArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PotionEffectArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PublicKeyArgument;
import me.earth.earthhack.impl.commands.packet.arguments.RecipeArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ResourceLocationArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScoreArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScoreObjectiveArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScorePlayerTeamArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SecretKeyArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ServerStatusResponseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SetArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ShortArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SoundEventArgument;
import me.earth.earthhack.impl.commands.packet.arguments.StatBaseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.StringArgument;
import me.earth.earthhack.impl.commands.packet.arguments.TextComponentArgument;
import me.earth.earthhack.impl.commands.packet.arguments.UUIDArgument;
import me.earth.earthhack.impl.commands.packet.arguments.Vec3dArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldBorderArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldTypeArgument;
import me.earth.earthhack.impl.commands.packet.array.ByteArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.FunctionArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.IntArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.ShortArrayArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.factory.DefaultFactory;
import me.earth.earthhack.impl.commands.packet.factory.PacketFactory;
import me.earth.earthhack.impl.commands.packet.factory.playerlistheaderfooter.SPacketPlayerListHeaderFooterFactory;
import me.earth.earthhack.impl.commands.packet.factory.playerlistitem.SPacketPlayerListItemFactory;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericCollectionArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericListArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericMapArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericNonNullListArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericSetArgument;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.StatBase;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapDecoration;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// TODO: Deobfuscate in VANILLA!!!
public class PacketCommandImpl extends Command implements Globals, PacketCommand
{
    private final Map<Class<? extends Packet<?>>, List<GenericArgument<?>>>
            generics;
    private final Map<Class<? extends Packet<?>>, PacketFactory>
            custom;
    private final Set<Class<? extends Packet<?>>>
            packets;
    private final Map<Class<?>, PacketArgument<?>>
            arguments;
    private final PacketFactory
            default_factory;

    public PacketCommandImpl()
    {
        super(new String[][]{{"packet"}, {"packet"}, {"index"}, {"arguments"}});
        CommandDescriptions.register(this, "Send/receive packets.");
        custom    = new HashMap<>();
        generics  = new HashMap<>();
        arguments = new HashMap<>();
        packets   = new HashSet<>();
        default_factory = new DefaultFactory(this);
        setup();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(String[] args)
    {
        if (args == null || args.length == 1)
        {
            ChatUtil.sendMessage("<PacketCommand> Use this command to" +
                    " send/receive a Packet. Remember to maybe escape your " +
                    "arguments with \", Arrays and Collection arguments are" +
                    " seperated by ], Map.Entries by ). This command should" +
                    " only be used if you know what you are doing!");
            return;
        }

        if (mc.player == null || mc.world == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "This command can only be used while ingame!");
            return;
        }

        Class<? extends Packet<?>> packetType = getPacket(args[1]);
        if (packetType == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't find packet: "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED + "!");
            return;
        }

        Type type = getNetHandlerType(packetType);
        if (type != INetHandlerPlayClient.class
                && type != INetHandlerPlayServer.class)
        {
            ChatUtil.sendMessage(TextColor.RED + "Packet "
                    + TextColor.WHITE + packetType.getName()
                    + TextColor.RED + " has unknown NetHandler type: "
                    + TextColor.WHITE + type + TextColor.RED + "!");
            return;
        }

        if (args.length == 2)
        {
            ChatUtil.sendMessage(
                    TextColor.RED + "Please specify a constructor index!");
            return;
        }

        PacketFactory gen = custom.getOrDefault(packetType, default_factory);
        Packet<?> packet;

        try
        {
            packet = gen.create(packetType, args);
        }
        catch (ArgParseException e)
        {
            ChatUtil.sendMessage(TextColor.RED + e.getMessage());
            return;
        }

        if (packet == null)
        {
            ChatUtil.sendMessage(TextColor.RED + "Packet for "
                    + TextColor.WHITE + MappingProvider.simpleName(packetType)
                    + TextColor.RED + " was null?!");
            return;
        }

        if (type == INetHandlerPlayServer.class)
        {
            ChatUtil.sendMessage(TextColor.GREEN + "Sending packet "
                    + TextColor.WHITE + MappingProvider.simpleName(packetType)
                    + TextColor.GREEN + " to server!");
            try
            {
                mc.player.connection.sendPacket(packet);
            }
            catch (Throwable t)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "An error occurred while sending packet "
                        + TextColor.WHITE
                        + MappingProvider.simpleName(packetType)
                        + TextColor.RED + ": " + t.getMessage());
                t.printStackTrace();
            }
        }
        else
        {
            ChatUtil.sendMessage(TextColor.GREEN
                    + "Attempting to receive packet "
                    + TextColor.WHITE + MappingProvider.simpleName(packetType)
                    + TextColor.GREEN + "!");

            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            List<Object> rs = BufferUtil.saveReleasableFields(packet);

            try
            {
                // this is required because some packets have
                // their fields initialized differently depending
                // on if they were constructed by ctr or buffer
                packet.writePacketData(buffer);
                packet.readPacketData(buffer);

                if (!NetworkUtil.receive(
                        (Packet<INetHandlerPlayClient>) packet))
                {
                    ChatUtil.sendMessage(
                        TextColor.RED + "The packet "
                            + TextColor.WHITE
                            + MappingProvider.simpleName(packetType)
                            + TextColor.RED + " got cancelled!");
                }
            }
            catch (Throwable t)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "An error occurred while receiving packet "
                        + TextColor.WHITE
                        + MappingProvider.simpleName(packetType)
                        + TextColor.RED + ": " + t.getMessage());
                t.printStackTrace();
            }
            finally
            {
                BufferUtil.release(rs);
                BufferUtil.releaseFields(packet);
                BufferUtil.releaseBuffer(buffer);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (mc.world == null || mc.player == null)
        {
            return inputs.setRest(TextColor.RED
                    + " <This command can only be used while ingame!>");
        }

        if (args.length <= 1 || args[1].isEmpty())
        {
            return super.getPossibleInputs(args);
        }

        Class<? extends Packet<?>> packet = getPacket(args[1]);
        if (packet == null)
        {
            return inputs.setRest(TextColor.RED + " not found!");
        }

        PacketFactory factory = custom.getOrDefault(packet, default_factory);
        return factory.getInputs(packet, args);
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        String[] args = completer.getArgs();
        if (args != null && args.length >= 2)
        {
            Class<? extends Packet<?>> p = getPacket(args[1]);
            PacketFactory factory = custom.getOrDefault(p, default_factory);
            switch (factory.onTabComplete(completer))
            {
                case PASS:
                    break;
                case RETURN:
                    return completer;
                case SUPER:
                    return super.onTabComplete(completer);
            }
        }

        if (completer.isSame())
        {
            return completer;
        }

        return super.onTabComplete(completer);
    }

    public void addGeneric(Class<? extends Packet<?>> type,
                           GenericArgument<?> argument)
    {
        generics.computeIfAbsent(type, v -> new ArrayList<>()).add(argument);
    }

    public <T extends Packet<?>> void addCustom(Class<T> type,
                                                PacketFactory factory)
    {
        custom.put(type, factory);
    }

    public <T> void addArgument(Class<T> type, PacketArgument<T> argument)
    {
        arguments.put(type, argument);
    }

    public void addPacket(Class<? extends Packet<?>> packet)
    {
        packets.add(packet);
    }

    @Override
    public Class<? extends Packet<?>> getPacket(String name)
    {
        for (Class<? extends Packet<?>> packet : packets)
        {
            if (TextUtil.startsWith(getName(packet), name))
            {
                return packet;
            }
        }

        return null;
    }

    @Override
    public Map<Class<? extends Packet<?>>,List<GenericArgument<?>>> getGenerics()
    {
        return generics;
    }

    @Override
    public Map<Class<? extends Packet<?>>, PacketFactory> getCustom()
    {
        return custom;
    }

    @Override
    public Set<Class<? extends Packet<?>>> getPackets()
    {
        return packets;
    }

    @Override
    public Map<Class<?>, PacketArgument<?>> getArguments()
    {
        return arguments;
    }

    @Override
    public String getName(Class<? extends Packet<?>> packet)
    {
        String simpleName = MappingProvider.simpleName(packet);
        if (packet.getSuperclass() != Object.class)
        {
            simpleName = MappingProvider.simpleName(packet.getSuperclass())
                    + "-" + simpleName;
        }

        return simpleName;
    }

    private void setup()
    {
        addCustom(SPacketPlayerListHeaderFooter.class,
                new SPacketPlayerListHeaderFooterFactory(this));
        addCustom(SPacketPlayerListItem.class,
                new SPacketPlayerListItemFactory(this));

        addArgument(boolean.class, new BooleanArgument());
        addArgument(int.class, new IntArgument());
        addArgument(float.class, new FloatArgument());
        addArgument(short.class, new ShortArgument());
        addArgument(long.class, new LongArgument());
        addArgument(double.class, new DoubleArgument());
        addArgument(byte.class, new ByteArgument());

        addArgument(String.class, new StringArgument());
        addArgument(BlockPos.class, new BlockPosArgument());
        addArgument(Vec3d.class, new Vec3dArgument());
        addArgument(Chunk.class, new ChunkArgument());

        addArgument(UUID.class, new UUIDArgument());
        addArgument(GameProfile.class, new GameProfileArgument());
        addArgument(ResourceLocation.class, new ResourceLocationArgument());

        addArgument(NBTTagCompound.class, new NBTTagCompoundArgument());
        addArgument(World.class, new WorldArgument());
        addArgument(ITextComponent.class, new TextComponentArgument());
        addArgument(PacketBuffer.class, new PacketBufferArgument());
        addArgument(Item.class, new ItemArgument());
        addArgument(ItemStack.class, new ItemStackArgument());
        addArgument(Block.class, new BlockArgument());
        addArgument(Potion.class, new PotionArgument());
        addArgument(PotionEffect.class, new PotionEffectArgument());
        addArgument(WorldType.class, new WorldTypeArgument());
        addArgument(SoundEvent.class, new SoundEventArgument());
        addArgument(PlayerCapabilities.class, new PlayerCapabilitiesArgument());
        addArgument(IRecipe.class, new RecipeArgument());

        addArgument(Entity.class, new EntityArgument());
        addArgument(EntityXPOrb.class, new EntityXPOrbArgument());
        addArgument(EntityPlayer.class, new EntityPlayerArgument());
        addArgument(EntityPainting.class, new EntityPaintingArgument());
        addArgument(EntityLivingBase.class, new EntityLivingBaseArgument());
        addArgument(IAttributeInstance.class, new AttributeArgument());

        addArgument(SPacketPlayerListItem.Action.class,
                new EnumArgument<>(SPacketPlayerListItem.Action.class));
        addArgument(SPacketWorldBorder.Action.class,
                new EnumArgument<>(SPacketWorldBorder.Action.class));
        addArgument(SPacketUpdateBossInfo.Operation.class,
                new EnumArgument<>(SPacketUpdateBossInfo.Operation.class));
        addArgument(SPacketCombatEvent.Event.class,
                new EnumArgument<>(SPacketCombatEvent.Event.class));
        addArgument(SPacketRecipeBook.State.class,
                new EnumArgument<>(SPacketRecipeBook.State.class));
        addArgument(SPacketTitle.Type.class,
                new EnumArgument<>(SPacketTitle.Type.class));

        addArgument(EntityEquipmentSlot.class,
                new EnumArgument<>(EntityEquipmentSlot.class));
        addArgument(EnumDifficulty.class,
                new EnumArgument<>(EnumDifficulty.class));
        addArgument(EnumParticleTypes.class,
                new EnumArgument<>(EnumParticleTypes.class));
        addArgument(SoundCategory.class,
                new EnumArgument<>(SoundCategory.class));
        addArgument(EnumConnectionState.class,
                new EnumArgument<>(EnumConnectionState.class));

        addArgument(CPacketClientStatus.State.class,
                new EnumArgument<>(CPacketClientStatus.State.class));
        addArgument(CPacketEntityAction.Action.class,
                new EnumArgument<>(CPacketEntityAction.Action.class));
        addArgument(CPacketPlayerDigging.Action.class,
                new EnumArgument<>(CPacketPlayerDigging.Action.class));
        addArgument(CPacketResourcePackStatus.Action.class,
                new EnumArgument<>(CPacketResourcePackStatus.Action.class));
        addArgument(CPacketSeenAdvancements.Action.class,
                new EnumArgument<>(CPacketSeenAdvancements.Action.class));

        addArgument(EnumFacing.class,
                new EnumArgument<>(EnumFacing.class));
        addArgument(ClickType.class,
                new EnumArgument<>(ClickType.class));
        addArgument(EnumHandSide.class,
                new EnumArgument<>(EnumHandSide.class));
        addArgument(EntityPlayer.EnumChatVisibility.class,
                new EnumArgument<>(EntityPlayer.EnumChatVisibility.class));
        addArgument(EnumHand.class,
                new EnumArgument<>(EnumHand.class));
        addArgument(ChatType.class,
                new EnumArgument<>(ChatType.class));
        addArgument(GameType.class,
                new EnumArgument<>(GameType.class));
        addArgument(MapDecoration.class, new MapDecorationArgument());
        addArgument(Advancement.class, new AdvancementArgument());

        // Useless Arguments
        addArgument(NonNullList.class, new NonNullListArgument());
        addArgument(Map.class, new MapArgument());
        addArgument(List.class, new ListArgument());
        addArgument(Collection.class, new CollectionArgument());
        addArgument(Set.class, new SetArgument());
        addArgument(Iterable.class, new IterableArgument());
        addArgument(SecretKey.class, new SecretKeyArgument());
        addArgument(PublicKey.class, new PublicKeyArgument());

        addArgument(WorldBorder.class, new WorldBorderArgument());
        addArgument(BossInfo.class, new BossInfoArgument());
        addArgument(ScoreObjective.class, new ScoreObjectiveArgument());
        addArgument(CombatTracker.class, new CombatTrackerArgument());
        addArgument(EntityDataManager.class, new EntityDataMangerArgument());
        addArgument(Score.class, new ScoreArgument());
        addArgument(ScorePlayerTeam.class, new ScorePlayerTeamArgument());

        addArgument(ServerStatusResponse.class,
                new ServerStatusResponseArgument());

        arguments.put(int[].class, new IntArrayArgument());
        arguments.put(byte[].class, new ByteArrayArgument());
        arguments.put(short[].class, new ShortArrayArgument());
        arguments.put(String[].class,
            new FunctionArrayArgument<>(
                String[].class, getArgument(String.class), String[]::new));
        arguments.put(ITextComponent[].class,
            new FunctionArrayArgument<>(
                ITextComponent[].class, getArgument(ITextComponent.class),
                    ITextComponent[]::new));
        try
        {
            Constructor<?> recipe = SPacketRecipeBook.class
                    .getDeclaredConstructor(SPacketRecipeBook.State.class,
                            List.class, List.class,
                            boolean.class, boolean.class);
            addGeneric(SPacketRecipeBook.class, new GenericListArgument<>(
                    recipe, 1, arguments.get(IRecipe.class)));
            addGeneric(SPacketRecipeBook.class, new GenericListArgument<>(
                    recipe, 2, arguments.get(IRecipe.class)));


            Constructor<?> teams = SPacketTeams.class
                    .getDeclaredConstructor(ScorePlayerTeam.class,
                            Collection.class, int.class);
            addGeneric(SPacketTeams.class, new GenericCollectionArgument<>(
                    teams, 1, arguments.get(String.class)));


            Constructor<?> advancement = SPacketAdvancementInfo.class
                    .getDeclaredConstructor(boolean.class,
                            Collection.class, Set.class,
                            Map.class);
            addGeneric(SPacketAdvancementInfo.class,
                new GenericCollectionArgument<>(
                        advancement, 1, arguments.get(Advancement.class)));
            addGeneric(SPacketAdvancementInfo.class,
                new GenericSetArgument<>(
                        advancement, 2, arguments.get(ResourceLocation.class)));
            addGeneric(SPacketAdvancementInfo.class,
                new GenericMapArgument<ResourceLocation, AdvancementProgress,
                    HashMap<ResourceLocation, AdvancementProgress>>(
                        HashMap.class, advancement, 3,
                        new ResourceLocationArgument(),
                        new AdvancementProgressArgument()));

            Constructor<?> properties = SPacketEntityProperties.class
                    .getDeclaredConstructor(int.class, Collection.class);
            addGeneric(SPacketEntityProperties.class,
                new GenericCollectionArgument<>(
                    properties, 1, arguments.get(IAttributeInstance.class)));


            Constructor<?> explosion = SPacketExplosion.class
                    .getDeclaredConstructor(double.class, double.class,
                            double.class, float.class, List.class, Vec3d.class);
            addGeneric(SPacketExplosion.class, new GenericListArgument<>(
                    explosion, 4, arguments.get(BlockPos.class)));


            Constructor<?> posLook = SPacketPlayerPosLook.class
                    .getDeclaredConstructor(double.class, double.class,
                            double.class, float.class, float.class, Set.class,
                            int.class);
            addGeneric(SPacketPlayerPosLook.class,
                new GenericSetArgument<>(posLook, 5,
                    new EnumArgument<>(SPacketPlayerPosLook.EnumFlags.class)));


            Constructor<?> windowItems = SPacketWindowItems.class
                    .getDeclaredConstructor(int.class, NonNullList.class);
            addGeneric(SPacketWindowItems.class,
                    new GenericNonNullListArgument<>(
                            windowItems, 1, arguments.get(ItemStack.class)));


            Constructor<?> maps = SPacketMaps.class.getDeclaredConstructor(
                    int.class, byte.class, boolean.class,
                    Collection.class, byte[].class, int.class,
                    int.class, int.class, int.class);
            addGeneric(SPacketMaps.class,
                    new GenericCollectionArgument<>(
                            maps, 3, arguments.get(MapDecoration.class)));


            Constructor<?> statistics = SPacketStatistics.class
                    .getDeclaredConstructor(Map.class);
            addGeneric(SPacketStatistics.class,
                    new GenericMapArgument<StatBase, Integer,
                            HashMap<StatBase, Integer>>(
                            HashMap.class, statistics, 0,
                            new StatBaseArgument(),
                            new IntArgument()));
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException(
                    "Constructor of a packet missing: " + e.getMessage());
        }

        for (Class<? extends Packet<?>> packet : PacketUtil.getAllPackets())
        {
            Type netHandler = getNetHandlerType(packet);
            if (netHandler != INetHandlerPlayClient.class
                    && netHandler != INetHandlerPlayServer.class)
            {
                continue;
            }

            if (!custom.containsKey(packet))
            {
                for (Constructor<?> ctr : packet.getDeclaredConstructors())
                {
                    for (Class<?> type : ctr.getParameterTypes())
                    {
                        if (!arguments.containsKey(type))
                        {
                            Earthhack.getLogger().error(
                                    "<PacketCommand>" +
                                            " No Argument found for: "
                                            + type.getName()
                                            + " in "
                                            + packet.getName());
                        }
                    }
                }
            }

            for (Class<? extends Packet<?>> alreadyExisting : packets)
            {
                if (alreadyExisting.getSimpleName()
                                   .equals(packet.getSimpleName())
                        && !alreadyExisting.equals(packet))
                {
                    Earthhack.getLogger().warn(alreadyExisting.getName()
                            + " SimpleName clashes with: " + packet.getName());
                }
            }

            addPacket(packet);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> PacketArgument<T> getArgument(Class<T> clazz)
    {
        return (PacketArgument<T>) arguments.get(clazz);
    }

    private Type getNetHandlerType(Class<? extends Packet<?>> packet)
    {
        Class<?> clazz = packet;

        do
        {
            Type[] types = clazz.getGenericInterfaces();
            for (Type genericInterface : types)
            {
                if (genericInterface instanceof ParameterizedType
                        && ((ParameterizedType) genericInterface)
                                        .getRawType() == Packet.class)
                {
                    for (Type type : ((ParameterizedType) genericInterface)
                                                .getActualTypeArguments())
                    {
                        return type;
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
        while (clazz != Object.class);

        return null;
    }

}
