package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;

public class Spectate extends DisablingModule implements CustomCommandModule
{
    protected final Setting<Boolean> stopMove =
        register(new BooleanSetting("NoMove", true));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> playerRotations =
        register(new BooleanSetting("Spectator-Rotate", false));

    protected EntityPlayerNoInterp fakePlayer;
    protected EntityPlayerNoInterp render;
    protected MovementInput input;

    protected EntityPlayer player;
    protected boolean spectating;

    public Spectate()
    {
        super("Spectate", Category.Player);
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerAttack(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerRemove(this));
        this.listeners.add(new ListenerAnimation(this));
        this.setData(new SimpleData(this, "FreeCam but more Vanilla."));
    }

    @Override
    public String getDisplayInfo()
    {
        if (spectating)
        {
            EntityPlayer thePlayer = player;
            return thePlayer != null ? thePlayer.getName() : null;
        }

        return null;
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length == 2 && mc.world != null && mc.player != null)
        {
            EntityPlayer player = null;
            for (EntityPlayer p : mc.world.playerEntities)
            {
                if (p != null && args[1].equalsIgnoreCase(p.getName()))
                {
                    player = p;
                    break;
                }
            }

            if (player != null)
            {
                specate(player);
                ModuleUtil.sendMessage(this,
                    TextColor.GREEN + "Now spectating: " + player.getName());
                return true;
            }
            else
            {
                Setting<?> setting = this.getSetting(args[1]);
                if (setting == null)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "Could not find setting or player "
                            + TextColor.WHITE
                            + args[1]
                            + TextColor.RED
                            + " in the Spectate module.");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs)
    {
        if (args.length == 1)
        {
            inputs.setCompletion(
                        TextUtil.substring(this.getName(), args[0].length()))
                  .setRest(" <setting/player> <value>");
            return true;
        }

        if (args.length != 2)
        {
            return false;
        }

        String next = LookUpUtil.findNextPlayerName(args[1]);
        if (next != null)
        {
            inputs.setCompletion(TextUtil.substring(next, args[1].length()));
            return true;
        }

        return false;
    }

    @Override
    public CustomCompleterResult complete(Completer completer)
    {
        if (!completer.isSame() && completer.getArgs().length == 2)
        {
            String player =
                LookUpUtil.findNextPlayerName(completer.getArgs()[1]);

            if (player != null)
            {
                return CustomCompleterResult.SUPER;
            }
        }

        return CustomCompleterResult.PASS;
    }

    @Override
    protected void onEnable()
    {
        mc.renderChunksMany = false;
        if (mc.player == null || mc.world == null)
        {
            this.disable();
            return;
        }

        // baritone could set this to its PlayerMovementInput
        if (mc.player.movementInput instanceof MovementInputFromOptions)
        {
            mc.player.movementInput = new MovementInput();
        }

        input = new MovementInputFromOptions(mc.gameSettings);

        render = new EntityPlayerNoInterp(mc.world);
        render.copyLocationAndAnglesFrom(mc.player);
        render.inventory = mc.player.inventory;
        render.inventoryContainer = mc.player.inventoryContainer;
        render.inventory.copyInventory(mc.player.inventory);
        render.setEntityBoundingBox(mc.player.getEntityBoundingBox());
        render.resetPositionToBB();

        fakePlayer = new EntityPlayerNoInterp(mc.world);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.inventoryContainer = mc.player.inventoryContainer;

        mc.world.addEntityToWorld(-10000, fakePlayer);
        PlayerUtil.FAKE_PLAYERS.put(-10000, fakePlayer);
        mc.entityRenderer.loadEntityShader(null);
    }

    @Override
    protected void onDisable()
    {
        mc.renderChunksMany = true;
        EntityPlayerSP playerSP = mc.player;
        if (playerSP != null)
        {
            // baritone
            MovementInput input = playerSP.movementInput;
            if (input != null && input.getClass() == MovementInput.class)
            {
                mc.addScheduledTask(() ->
                {
                    playerSP.movementInput =
                            new MovementInputFromOptions(mc.gameSettings);
                });
            }
        }

        EntityPlayer specPlayer = player;
        if (spectating)
        {
            if (specPlayer != null)
            {
                ((IEntityNoInterp) specPlayer).setNoInterping(true);
            }

            this.spectating = false;
        }

        mc.addScheduledTask(() ->
        {
            player = null;
        });

        if (mc.world != null)
        {
            mc.addScheduledTask(() ->
            {
                if (mc.world != null)
                {
                    this.player = null;
                    mc.world.removeEntity(fakePlayer);
                    PlayerUtil.FAKE_PLAYERS.remove(fakePlayer.getEntityId());
                }
            });
        }
    }

    public boolean shouldTurn()
    {
        return !spectating || player == null || playerRotations.getValue();
    }

    public void specate(EntityPlayer player)
    {
        if (this.isEnabled())
        {
            this.disable();
        }

        this.spectating = true;
        this.player = player;

        ((IEntityNoInterp) player).setNoInterping(false);

        this.enable();
    }

    public EntityPlayer getRender()
    {
        return spectating ? (player == null ? mc.player : player) : render;
    }

    public EntityPlayer getFake()
    {
        return fakePlayer;
    }

}
