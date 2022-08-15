package me.earth.earthhack.impl.commands.packet.factory.playerlistheaderfooter;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.PacketCommand;
import me.earth.earthhack.impl.commands.packet.arguments.TextComponentArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.factory.PacketFactory;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;

import java.lang.reflect.Field;

public class SPacketPlayerListHeaderFooterFactory implements PacketFactory
{
    private static final TextComponentArgument TEXT_COMPONENT_ARGUMENT =
            new TextComponentArgument();

    private final PacketCommand command;

    public SPacketPlayerListHeaderFooterFactory(PacketCommand command)
    {
        this.command = command;
    }

    @Override
    public Packet<?> create(Class<? extends Packet<?>> clazz, String[] args)
            throws ArgParseException
    {
        if (!SPacketPlayerListHeaderFooter.class.isAssignableFrom(clazz))
        {
            throw new IllegalStateException("This definitely shouldn't happen!"
                    + " SPacketPlayerListHeaderFooterFactory got: "
                    + clazz.getName());
        }

        if (args.length != 5)
        {
            throw new ArgParseException("Expected 5 Arguments for" +
                    " SPacketPlayerListHeaderFooter, but found: "
                            + args.length + "!");
        }

        ITextComponent header = TEXT_COMPONENT_ARGUMENT.fromString(args[3]);
        ITextComponent footer = TEXT_COMPONENT_ARGUMENT.fromString(args[4]);

        SPacketPlayerListHeaderFooter p = new SPacketPlayerListHeaderFooter();
        try
        {
            setHeaderFooter(p, header, footer);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            throw new ArgParseException("Couldn't set header/footer: "
                    + e.getMessage());
        }

        return p;
    }

    public static void setHeaderFooter(SPacketPlayerListHeaderFooter packet, ITextComponent header, ITextComponent footer) throws NoSuchFieldException, IllegalAccessException {
        Field headerField = ReflectionUtil.getField(
            SPacketPlayerListHeaderFooter.class,
            "header", "field_179703_a", "a");

        Field footerField = ReflectionUtil.getField(
            SPacketPlayerListHeaderFooter.class,
            "footer", "field_179702_b", "b");

        headerField.setAccessible(true);
        headerField.set(packet, header);

        footerField.setAccessible(true);
        footerField.set(packet, footer);
    }

    @Override
    public PossibleInputs getInputs(Class<? extends Packet<?>> clazz,
                                    String[] args)
    {
        if (!SPacketPlayerListHeaderFooter.class.isAssignableFrom(clazz))
        {
            throw new IllegalStateException("This definitely shouldn't happen!"
                    + " SPacketPlayerListHeaderFooterFactory got: "
                    + clazz.getName());
        }

        PossibleInputs inputs = PossibleInputs.empty();
        switch (args.length)
        {
            case 2:
                String name = command.getName(clazz);
                return inputs.setRest(" <index>")
                    .setCompletion(TextUtil.substring(name, args[1].length()));
            case 3:
                String rest = TEXT_COMPONENT_ARGUMENT.getPossibleInputs(null)
                                                     .getRest();
                return inputs.setRest(" " + rest + " " + rest);
            case 4:
                String rest1 = TEXT_COMPONENT_ARGUMENT.getPossibleInputs(null)
                                                      .getRest();
                inputs = TEXT_COMPONENT_ARGUMENT
                            .getPossibleInputs(args[3]);
                return inputs.setRest(inputs.getRest() + " " + rest1);
            case 5:
                return TEXT_COMPONENT_ARGUMENT.getPossibleInputs(args[4]);
            default:
        }

        return inputs.setRest(
                TextColor.RED + "Too many Arguments (max 2).");
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
