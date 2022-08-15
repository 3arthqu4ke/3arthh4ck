package me.earth.earthhack.impl.modules.misc.autoreconnect.util;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.gui.util.IGuiDisconnected;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class ReconnectScreen extends GuiDisconnected
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    /** Timer measuring handling the delay. */
    private final StopWatch timer = new StopWatch();
    /** Accessor for the parentGui to get Screen, Reason and Message from. */
    private final IGuiDisconnected parent;
    /** The ServerData we reconnect to. */
    private final ServerData data;
    /** The delay until we reconnect. */
    private final int delay;
    /** A button to turn reconnecting on and off. */
    private GuiButton reconnectButton;
    /** Marks if no data is available. */
    private boolean noData;
    /** Marks if we should reconnect. */
    private boolean reconnect;
    /** Marks the time we stopped the timer */
    private long time;

    public ReconnectScreen(IGuiDisconnected parent,
                           ServerData serverData,
                           int delay)
    {
        super(parent.getParentScreen(), parent.getReason(), parent.getMessage());
        this.parent    = parent;
        this.data      = serverData;
        this.delay     = delay;
        this.reconnect = true;
        this.time = System.currentTimeMillis();
        this.mc = Minecraft.getMinecraft();
        timer.reset();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        int textHeight = ((IGuiDisconnected) this).getMultilineMessage().size() * this.fontRenderer.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), (data == null ? TextColor.RED : TextColor.WHITE) + "Reconnect"));
        this.reconnectButton = new GuiButton(2, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 23, getButtonString());
        this.buttonList.add(reconnectButton);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 46, I18n.format("gui.toMenu")));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (button.id == 1)
        {
            connect();
        }
        else if (button.id == 2)
        {
            reconnect = !reconnect;
            this.time = timer.getTime();
            this.reconnectButton.displayString = getButtonString();
        }
    }

    @Override
    public void updateScreen()
    {
        if (!reconnect)
        {
            timer.setTime(System.currentTimeMillis() - time);
        }

        if (noData)
        {
            if (timer.passed(3000))
            {
                mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }
        }
        else if (timer.passed(delay) && reconnect)
        {
            connect();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        String text = getReconnectString();
        Managers.TEXT.drawStringWithShadow(text,
                width / 2.0f - Managers.TEXT.getStringWidth(text) / 2.0f,
                16,
                0xffffffff);
    }

    private void connect()
    {
        ServerData serverData = data == null ? mc.getCurrentServerData() : data;
        if (serverData != null)
        {
            if (PINGBYPASS.isEnabled())
            {
                mc.displayGuiScreen(
                        new GuiConnectingPingBypass(parent.getParentScreen(),
                                                    mc,
                                                    serverData));
            }
            else
            {
                mc.displayGuiScreen(new GuiConnecting(parent.getParentScreen(),
                                                      mc,
                                                      serverData));
            }
        }
        else
        {
            noData = true;
            timer.reset();
        }
    }

    private String getButtonString()
    {
        return "AutoReconnect: "
                + (reconnect ? TextColor.GREEN + "On" : TextColor.RED + "Off");
    }

    private String getReconnectString()
    {
        float time = MathUtil.round((delay -
                (reconnect ? timer.getTime() : this.time)) / 1000.0f, 1);
        return  noData
                    ? (TextColor.RED + "No ServerData found!")
                    : ("Reconnecting in " + (time <= 0 ? "0.0" : time) + "s.");
    }

}
