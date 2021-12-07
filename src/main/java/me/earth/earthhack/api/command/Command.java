package me.earth.earthhack.api.command;

import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class Command implements Nameable
{
    private final String name;
    private final String fullUsage;
    private final String[][] usage;
    private final boolean hidden;

    public Command(String[][] usage)
    {
        this(usage, false);
    }

    public Command(String[][] usage, boolean hidden)
    {
        if (usage == null || usage.length == 0 || usage[0].length != 1)
        {
            throw new IllegalArgumentException("Usage of command needs to be " +
                "an 2 dimensional array with a length > 0 and the first entry" +
                " needs to have a length of 1.");
        }

        this.name = usage[0][0];
        this.usage = usage;
        this.hidden = hidden;
        this.fullUsage = concatenateUsage(0);
    }

    @Override
    public String getName()
    {
        return name;
    }

    public boolean fits(String[] args)
    {
        return args[0].length() > 0
                && TextUtil.startsWith(name, args[0]);
    }

    /**
     * Called when a chatmessage starting with the prefix is sent
     * and fits returns true for the string array. Keep in mind that
     * the current GuiScreen will be closed after this method
     * has been called!
     *
     * @param args the input, length >= 1.
     */ // TODO: param with raw input?
    public abstract void execute(String[] args);

    /**
     * Used to render possible inputs in the command line.
     *
     * @param args the input, length >= 1.
     * @return a string with possible usage.
     */
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args == null || args.length == 0)
        {
            return PossibleInputs.empty();
        }

        if (args.length == 1)
        {
            String completion =
                TextUtil.substring(name, args[0].length());
            String rest =
                TextUtil.substring(this.getFullUsage(), name.length());
            return new PossibleInputs(completion, rest);
        }

        if (args.length <= usage.length)
        {
            String last = getLast(args);
            String completion =
                TextUtil.substring(last, args[args.length - 1].length());
            return new PossibleInputs(completion,
                                      concatenateUsage(args.length));
        }

        return PossibleInputs.empty();
    }

    /**
     * Used to tab complete commands. Override to
     * implement your own tab-complete behaviour.
     *
     * @param completer the tab completer.
     * @return the tab completer.
     */
    public Completer onTabComplete(Completer completer)
    {
        if (completer.isSame())
        {
            if (completer.getArgs().length <= this.usage.length)
            {
                String[] args = usage[completer.getArgs().length - 1];
                int i;
                for (i = 0; i < args.length; i++)
                {
                    if (args[i].equalsIgnoreCase(
                            completer.getArgs()[
                                    completer.getArgs().length - 1]))
                    {
                        break;
                    }
                }

                String arg = i >= args.length - 1 ? args[0] : args[i + 1];
                String newInitial =
                        completer.getInitial().trim().substring(
                            0,
                            completer.getInitial().trim().length()
                                    - completer.getArgs()[
                                            completer.getArgs().length
                                                    - 1].length());
                completer.setResult(newInitial + arg);
                return completer;
            }
        }

        PossibleInputs inputs = getPossibleInputs(completer.getArgs());
        if (!inputs.getCompletion().isEmpty())
        {
            completer.setResult(completer.getInitial().trim()
                                    + inputs.getCompletion());
            return completer;
        }

        completer.setMcComplete(true);
        return completer;
    }

    public String[][] getUsage()
    {
        return usage;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public String getFullUsage()
    {
        return fullUsage;
    }

    private String getLast(String[] args)
    {
        if (args.length <= usage.length)
        {
            String last = args[args.length - 1];
            String[] array = usage[args.length - 1];
            for (String string : array)
            {
                if (TextUtil.startsWith(string, last))
                {
                    return string;
                }
            }
        }

        return "";
    }

    private String concatenateUsage(int index)
    {
        if (usage.length == 1)
        {
            return this.name;
        }
        else if (index >= usage.length)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder(index == 0 ? this.name : "");
        for (int j = index == 0 ? 1 : index; j < usage.length; j++)
        {
            builder.append(" <");
            for (int i = 0; i < usage[j].length; i++)
            {
                builder.append(usage[j][i]).append("/");
            }

            builder.replace(builder.length() - 1, builder.length(), ">");
        }

        return builder.toString();
    }

}
