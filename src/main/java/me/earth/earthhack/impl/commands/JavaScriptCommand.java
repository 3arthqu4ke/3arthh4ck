package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.commands.util.EarthhackJsBridge;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JavaScriptCommand extends Command
        implements Globals, GlobalExecutor, CommandScheduler
{
    private static final String[] EMPTY = new String[0];

    private final Map<String, String[]> arguments = new HashMap<>();
    private final ScriptEngine engine;
    private final boolean replaceRn;
    private final boolean invalid;
    private final boolean jsNull;

    public JavaScriptCommand()
    {
        super(new String[][]{{"javascript"}, {"code"}});
        CommandDescriptions.register(this, "Allows you to execute JavaScript.");

        Argument<Boolean> arg = DevArguments.getInstance().getArgument("jsrn");
        replaceRn = arg == null || arg.getValue();
        arg = DevArguments.getInstance().getArgument("jsnull");
        jsNull = arg == null || arg.getValue();

        boolean invalid = false;
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine theEngine = factory.getEngineByName("JavaScript");
        if (theEngine == null)
        {
            Earthhack.getLogger().warn("JavaScript was null, using nashorn!");
            theEngine = factory.getEngineByName("nashorn");
            if (theEngine == null)
            {
                invalid = true;
            }
        }

        this.invalid = invalid;
        if (invalid)
        {
            engine = null;
            return;
        }

        engine = theEngine;
        EarthhackJsBridge bridge = new EarthhackJsBridge();
        engine.put("Earthhack", bridge);

        setupArguments();
    }

    @Override
    public boolean fits(String[] args)
    {
        String low = args[0].toLowerCase();
        return "javascript".startsWith(low) || low.startsWith("javascript");
    }

    @Override
    public void execute(String[] args)
    {
        if (PingBypass.isServer() && !PingBypass.CONFIG.enableJavaScript())
        {
            ChatUtil.sendMessage(
                TextColor.RED + "JavaScript is not enabled" +
                    " on this PingBypass server!");
            return;
        }

        if (invalid)
        {
            ChatUtil.sendMessage(TextColor.RED
                + "Your Java version doesn't support nashorn or JavaScript!");
            return;
        }

        long timeout = 5000;
        boolean noTimeout = false;
        String s = args[0].toLowerCase().replace("javascript", "");
        if (s.equals("notimeout"))
        {
            noTimeout = true;
        }
        else if (!s.isEmpty())
        {
            try
            {
                timeout = Long.parseLong(s);
            }
            catch (NumberFormatException e)
            {
                ChatUtil.sendMessage(TextColor.RED + "Couldn't parse timeout: "
                        + TextColor.WHITE + s + TextColor.RED + ".");
                return;
            }

            if (timeout < 0)
            {
                ChatUtil.sendMessage(
                        TextColor.RED + "Timeout can't be negative!");
                return;
            }
        }

        if (args.length <= 1)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "This command allows you to execute JavaScript Code."
                    + TextColor.AQUA
                    + " Tip:"
                    + TextColor.WHITE
                    + " Use the functions offered by \"Math\", "
                    + "or \"Earthhack\".");
            return;
        }

        String code = CommandUtil.concatenate(args, 1);
        // we use the FIXED_EXECUTOR because this could get called
        // by a recursive macro causing 10000 threads to start.
        Future<?> future = FIXED_EXECUTOR.submit(new SafeRunnable()
        {
            @Override
            public void runSafely() throws Throwable
            {
                Object o = engine.eval(code);
                if (o != null || jsNull)
                {
                    mc.addScheduledTask(() -> ChatUtil.sendMessage(o + ""));
                }
            }

            @Override
            public void handle(Throwable t)
            {
                String message;
                if (replaceRn)
                {
                    message = t.getMessage().replace("\r\n", "\n");
                }
                else
                {
                    message = t.getMessage();
                }

                mc.addScheduledTask(() -> ChatUtil.sendMessage(
                    "<JavaScript> " + TextColor.RED + "Error: " + message));
            }
        });

        if (noTimeout)
        {
            return;
        }
        // set timeout with scheduler, that way we dont block the mainthread.
        long finalTimeout = timeout;
        SCHEDULER.schedule(() ->
        {
            if (future.cancel(true))
            {
                double t = MathUtil.round(finalTimeout / 1000.0, 2);
                mc.addScheduledTask(() -> ChatUtil.sendMessage("<JavaScript> "
                        + TextColor.RED
                        + t
                        + " seconds passed, your js timed out!"));
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.isSame() && completer.getArgs().length > 1)
        {
            String[] args = completer.getArgs();
            String last = args[args.length - 1];
            if (last.isEmpty())
            {
                if (args.length == 2)
                {
                    return completer;
                }
                else
                {
                    return super.onTabComplete(completer);
                }
            }

            for (Map.Entry<String, String[]> entry :
                    arguments.entrySet())
            {
                if (last.startsWith(entry.getKey()))
                {
                    String[] l = entry.getValue();
                    if (l.length == 0)
                    {
                        continue;
                    }

                    if (entry.getKey().length() == last.length())
                    {
                        completer.setResult(completer.getInitial() + l[0]);
                        return completer;
                    }

                    if (l.length == 1)
                    {
                        return completer;
                    }

                    String r = Commands.getPrefix()
                            + CommandUtil.concatenate(args, 0, args.length - 1);
                    if (last.equals(l[l.length - 1]))
                    {
                        String s = entry.getKey() + l[0];
                        return completer.setResult(r + " " + s);
                    }

                    boolean found = false;
                    for (String s : entry.getValue())
                    {
                        String o = entry.getKey() + s;
                        if (found)
                        {
                            return completer.setResult(r + " " + o);
                        }

                        if (o.equals(last))
                        {
                            found = true;
                        }
                    }
                }
            }
        }

        return super.onTabComplete(completer);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length == 1
            && args[0].length() > 10
            && "javascriptnotimeout".startsWith(args[0].toLowerCase()))
        {
            return new PossibleInputs(
                    TextUtil.substring("JavaScriptNoTimeout", args[0].length()),
                    " <code>");
        }

        if (args.length >= 2)
        {
            PossibleInputs inputs = PossibleInputs.empty();
            String last = args[args.length - 1];
            if (!last.isEmpty())
            {
                for (Map.Entry<String, String[]> entry :
                        arguments.entrySet())
                {
                    if (entry.getKey().startsWith(last)
                            || last.startsWith(entry.getKey()))
                    {
                        if (last.length() < entry.getKey().length())
                        {
                            return inputs.setCompletion(
                                TextUtil.substring(
                                    entry.getKey(), last.length()));
                        }
                        else
                        {
                            for (String s : entry.getValue())
                            {
                                String o = entry.getKey() + s;
                                if (o.startsWith(last))
                                {
                                    return inputs.setCompletion(
                                        TextUtil.substring(
                                                o, last.length()));
                                }
                            }
                        }
                    }
                }
            }

            boolean string = false;
            Deque<Character> lastOpened = new LinkedList<>();
            for (int i = 1; i < args.length; i++)
            {
                String s = args[i];
                for (int j = 0; j < s.length(); j++)
                {
                    char c = s.charAt(j);
                    if (string && c != '\'')
                    {
                        continue;
                    }

                    switch (c)
                    {
                        case '\'':
                            if (!lastOpened.isEmpty()
                                && lastOpened.getLast() == '\'')
                            {
                                lastOpened.pollLast();
                                string = false;
                                break;
                            }

                            lastOpened.add('\'');
                            string = true;
                            break;
                        case '{':
                            lastOpened.add('}');
                            break;
                        case '}':
                            Character l = lastOpened.pollLast();
                            if (l == null || l == ')')
                            {
                                return inputs.setRest(
                                    "Did you forget a \")\" somewhere?");
                            }
                            break;
                        case '(':
                            lastOpened.add(')');
                            break;
                        case ')':
                            l = lastOpened.pollLast();
                            if (l == null || l == '}')
                            {
                                return inputs.setRest(
                                    "Did you forget a \"}\" somewhere?");
                            }
                            break;
                        default:
                    }
                }
            }

            Character opened = lastOpened.pollLast();
            if (opened != null)
            {
                return inputs.setCompletion(opened == '\''
                                                ? "'"
                                                : last.isEmpty()
                                                    ? "  " + opened
                                                    : " " + opened);
            }

            return inputs;
        }

        return super.getPossibleInputs(args);
    }

    private void setupArguments()
    {
        arguments.put("function",  EMPTY);
        arguments.put("return",    EMPTY);
        arguments.put("Infinity",  EMPTY);
        arguments.put("NaN",       EMPTY);
        arguments.put("null",      EMPTY);
        arguments.put("isNaN(",    EMPTY);
        arguments.put("isFinite(", EMPTY);
        arguments.put("eval(",     EMPTY);

        arguments.put("Earthhack.", new String[]{ "command(", "isEnabled(" });

        List<String> mathArgs = new ArrayList<>(42);
        mathArgs.add("abs(");
        mathArgs.add("acos(");
        mathArgs.add("acosh(");
        mathArgs.add("asin(");
        mathArgs.add("asinh(");
        mathArgs.add("atan(");
        mathArgs.add("atanh(");
        mathArgs.add("cbrt(");
        mathArgs.add("ceil(");
        mathArgs.add("clz32(");
        mathArgs.add("cos(");
        mathArgs.add("cosh(");
        mathArgs.add("exp(");
        mathArgs.add("expm1(");
        mathArgs.add("floor(");
        mathArgs.add("fround(");
        mathArgs.add("hypot(");
        mathArgs.add("imul(");
        mathArgs.add("log(");
        mathArgs.add("log1p(");
        mathArgs.add("log10(");
        mathArgs.add("log2(");
        mathArgs.add("max(");
        mathArgs.add("min(");
        mathArgs.add("pow(");
        mathArgs.add("random(");
        mathArgs.add("round(");
        mathArgs.add("sign(");
        mathArgs.add("sin(");
        mathArgs.add("sinh(");
        mathArgs.add("sqrt(");
        mathArgs.add("tan(");
        mathArgs.add("tanh(");
        mathArgs.add("trunc(");
        mathArgs.add("E");
        mathArgs.add("LN2");
        mathArgs.add("LN10");
        mathArgs.add("LOG2E");
        mathArgs.add("LOG10E");
        mathArgs.add("PI");
        mathArgs.add("SQRT1_2");
        mathArgs.add("SQRT2");

        arguments.put("Math.", mathArgs.toArray(new String[0]));
    }

}
