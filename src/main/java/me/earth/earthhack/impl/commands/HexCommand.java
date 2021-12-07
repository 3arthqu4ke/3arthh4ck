package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.awt.*;

public class HexCommand extends Command
{
    public HexCommand()
    {
        super(new String[][]{{"hex"},
                             {"red", "num"},
                             {"green", "radix"},
                             {"blue"},
                             {"alpha"}});
        CommandDescriptions.register(this, "ColorSettings are hard to use if" +
                " you don't know how hex numbers work. This command helps" +
                " you with converting numbers to different radixes.");
    }

    @Override
    public void execute(String[] args)
    {
        switch (args.length)
        {
            case 1:
                ChatUtil.sendMessage("This command converts a Number (1 arg)"
                        + " or a Color (3-4 args) to a 32-Bit-Hex-String."
                        + " If you choose 2 args you can convert a Number"
                        + " with the given radix. (hex-num, 16 for example).");
                break;
            case 2:
                try
                {
                    int n = (int) Long.parseLong(args[1]);
                    String r = Integer.toHexString(n).toUpperCase();
                    ChatUtil.sendMessage("Hex value of "
                                            + TextColor.AQUA
                                            + args[1]
                                            + TextColor.WHITE
                                            + " is "
                                            + TextColor.RED
                                            + r
                                            + ".");
                }
                catch (Exception e)
                {
                    ChatUtil.sendMessage("<Hex> "
                                        + TextColor.RED
                                        + args[1]
                                        + " was not parsable.");
                }
                break;
            case 3:
                int radix = 16;

                try
                {
                    radix = Integer.parseInt(args[2]);
                }
                catch (Exception e)
                {
                    ChatUtil.sendMessage("<Hex> "
                            + TextColor.RED
                            + args[2]
                            + " was not parsable, continuing with radix 16.");
                }

                try
                {
                    int r = (int) Long.parseLong(args[1], radix);
                    ChatUtil.sendMessage("Hex value of "
                            + TextColor.AQUA
                            + args[1]
                            + TextColor.WHITE
                            + " in radix "
                            + TextColor.AQUA
                            + radix
                            + TextColor.WHITE
                            + " is "
                            + TextColor.RED
                            + r
                            + ".");
                }
                catch (Exception e)
                {
                    ChatUtil.sendMessage("<Hex> "
                            + TextColor.RED
                            + args[1]
                            + " was not parsable.");
                }
                break;
            default:
                int r;
                int g;
                int b;

                try
                {
                    r = Integer.parseInt(args[1]);
                    g = Integer.parseInt(args[2]);
                    b = Integer.parseInt(args[3]);
                }
                catch (Exception e)
                {
                    ChatUtil.sendMessage("<Hex> "
                            + TextColor.RED
                            + "An Argument was not parsable.");
                    break;
                }

                int a = 255;

                if (args.length > 5)
                {
                    try
                    {
                        a = Integer.parseInt(args[4]);
                    }
                    catch (Exception e)
                    {
                        ChatUtil.sendMessage("<Hex> "
                                + TextColor.RED
                                + "Alpha was not parsable,"
                                + " continuing with 255.");
                    }
                }

                Color color = new Color(r, g, b, a);
                ChatUtil.sendMessage("Hex value of "
                                    + TextColor.CUSTOM
                                    + TextUtil.get32BitString(color.getRGB())
                                    +"["
                                    + r + ", "
                                    + g + ", "
                                    + b + ", "
                                    + a
                                    + "] "
                                    + TextColor.WHITE
                                    + "is "
                                    + TextColor.AQUA
                                    + TextUtil.get32BitString(color.getRGB()));
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 1)
        {
            return super.getPossibleInputs(args).setCompletion("");
        }

        return super.getPossibleInputs(args);
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.getArgs().length > 1)
        {
            return completer.setMcComplete(true);
        }

        return super.onTabComplete(completer);
    }

}
