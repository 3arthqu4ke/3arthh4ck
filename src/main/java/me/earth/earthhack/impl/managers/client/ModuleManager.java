package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.colors.Colors;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.modules.client.debug.Debug;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.notifications.Notifications;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.rotationbypass.Compatibility;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.tab.TabModule;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.antitrap.AntiTrap;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.combat.bedbomb.BedBomb;
import me.earth.earthhack.impl.modules.combat.bomber.CrystalBomber;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import me.earth.earthhack.impl.modules.combat.holefiller.HoleFiller;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import me.earth.earthhack.impl.modules.combat.selftrap.SelfTrap;
import me.earth.earthhack.impl.modules.combat.snowballer.Snowballer;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.combat.webaura.WebAura;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAim;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPackets;
import me.earth.earthhack.impl.modules.misc.antipotion.AntiPotion;
import me.earth.earthhack.impl.modules.misc.antivanish.AntiVanish;
import me.earth.earthhack.impl.modules.misc.autocraft.AutoCraft;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;
import me.earth.earthhack.impl.modules.misc.autofish.AutoFish;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnect;
import me.earth.earthhack.impl.modules.misc.autoregear.AutoRegear;
import me.earth.earthhack.impl.modules.misc.autorespawn.AutoRespawn;
import me.earth.earthhack.impl.modules.misc.buildheight.BuildHeight;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import me.earth.earthhack.impl.modules.misc.logger.Logger;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;
import me.earth.earthhack.impl.modules.misc.mobowner.MobOwner;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;
import me.earth.earthhack.impl.modules.misc.nohandshake.NoHandShake;
import me.earth.earthhack.impl.modules.misc.nointeract.NoInteract;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.misc.nosoundlag.NoSoundLag;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.misc.packetdelay.PacketDelay;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.impl.modules.misc.portals.Portals;
import me.earth.earthhack.impl.modules.misc.rpc.RPC;
import me.earth.earthhack.impl.modules.misc.settingspoof.SettingSpoof;
import me.earth.earthhack.impl.modules.misc.skinblink.SkinBlink;
import me.earth.earthhack.impl.modules.misc.spammer.Spammer;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSync;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import me.earth.earthhack.impl.modules.misc.truedurability.TrueDurability;
import me.earth.earthhack.impl.modules.movement.anchor.Anchor;
import me.earth.earthhack.impl.modules.movement.antimove.NoMove;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.entitycontrol.EntityControl;
import me.earth.earthhack.impl.modules.movement.entityspeed.EntitySpeed;
import me.earth.earthhack.impl.modules.movement.fastswim.FastSwim;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;
import me.earth.earthhack.impl.modules.movement.icespeed.IceSpeed;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.nofall.NoFall;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.movement.reversestep.ReverseStep;
import me.earth.earthhack.impl.modules.movement.safewalk.SafeWalk;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.tickshift.TickShift;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.autotool.AutoTool;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.player.cleaner.Cleaner;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.liquids.LiquidInteract;
import me.earth.earthhack.impl.modules.player.mcp.MiddleClickPearl;
import me.earth.earthhack.impl.modules.player.multitask.MultiTask;
import me.earth.earthhack.impl.modules.player.ncptweaks.NCPTweaks;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.modules.player.nohunger.NoHunger;
import me.earth.earthhack.impl.modules.player.noinventorydesync.InventorySync;
import me.earth.earthhack.impl.modules.player.norotate.NoRotate;
import me.earth.earthhack.impl.modules.player.raytrace.RayTrace;
import me.earth.earthhack.impl.modules.player.reach.Reach;
import me.earth.earthhack.impl.modules.player.replenish.Replenish;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;
import me.earth.earthhack.impl.modules.player.sorter.Sorter;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.crosshair.Crosshair;
import me.earth.earthhack.impl.modules.render.crystalchams.CrystalChams;
import me.earth.earthhack.impl.modules.render.crystalscale.CrystalScale;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.fullbright.Fullbright;
import me.earth.earthhack.impl.modules.render.handchams.HandChams;
import me.earth.earthhack.impl.modules.render.holeesp.HoleESP;
import me.earth.earthhack.impl.modules.render.itemchams.ItemChams;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import me.earth.earthhack.impl.modules.render.newchunks.NewChunks;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.penis.Penis;
import me.earth.earthhack.impl.modules.render.popchams.PopChams;
import me.earth.earthhack.impl.modules.render.rainbowenchant.RainbowEnchant;
import me.earth.earthhack.impl.modules.render.search.Search;
import me.earth.earthhack.impl.modules.render.skeleton.Skeleton;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import me.earth.earthhack.impl.modules.render.tracers.Tracers;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import me.earth.earthhack.impl.modules.render.trajectories.Trajectories;
import me.earth.earthhack.impl.modules.render.viewclip.CameraClip;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModel;
import me.earth.earthhack.impl.modules.render.voidesp.VoidESP;
import me.earth.earthhack.impl.modules.render.waypoints.WayPoints;
import me.earth.earthhack.impl.modules.render.weather.Weather;
import me.earth.earthhack.impl.modules.render.xray.XRay;

import java.util.ArrayList;

public class ModuleManager extends IterationRegister<Module>
{
    public void init()
    {
        Earthhack.getLogger().info("Initializing Modules.");
        this.forceRegister(new AutoConfig());
        this.forceRegister(new ClickGui());
        this.forceRegister(new Colors());
        this.forceRegister(new Commands());
        this.forceRegister(new Debug());
        this.forceRegister(new FontMod());
        this.forceRegister(new HUD());
        this.forceRegister(new Management());
        this.forceRegister(new Notifications());
        this.forceRegister(new Compatibility());
        this.forceRegister(new Safety());
        this.forceRegister(new ServerModule());
        this.forceRegister(new TabModule());
        this.forceRegister(new Media());
        this.forceRegister(new HudEditor());

        this.forceRegister(new AntiSurround());
        this.forceRegister(new AntiTrap());
        this.forceRegister(new Auto32k());
        this.forceRegister(new AnvilAura());
        this.forceRegister(new AutoArmor());
        this.forceRegister(new AutoCrystal());
        this.forceRegister(new AutoTrap());
        this.forceRegister(new BedBomb());
        this.forceRegister(new BowSpam());
        this.forceRegister(new BowKiller());
        this.forceRegister(new Criticals());
        this.forceRegister(new CrystalBomber());
        this.forceRegister(new HoleFiller());
        this.forceRegister(new KillAura());
        this.forceRegister(new LegSwitch());
        this.forceRegister(new Offhand());
        this.forceRegister(new PistonAura());
        this.forceRegister(new Surround());
        this.forceRegister(new Snowballer());
        this.forceRegister(new SelfTrap());
        this.forceRegister(new WebAura());

        this.forceRegister(new Announcer());
        this.forceRegister(new AntiAim());
        this.forceRegister(new AntiPackets());
        this.forceRegister(new AntiPotion());
        this.forceRegister(new AntiVanish());
        this.forceRegister(new AutoEat());
        this.forceRegister(new AutoFish());
        this.forceRegister(new AutoLog());
        this.forceRegister(new AutoReconnect());
        this.forceRegister(new AutoRespawn());
        this.forceRegister(new BuildHeight());
        this.forceRegister(new Chat());
        this.forceRegister(new ExtraTab());
        this.forceRegister(new Logger());
        this.forceRegister(new MCF());
        this.forceRegister(new MobOwner());
        this.forceRegister(new NoAFK());
        this.forceRegister(new NoHandShake());
        this.forceRegister(new NoInteract());
        this.forceRegister(new NoInterp());
        this.forceRegister(new NoSoundLag());
        this.forceRegister(new Nuker());
        this.forceRegister(new Packets());
        this.forceRegister(new PingSpoof());
        this.forceRegister(new Portals());
        this.forceRegister(new SettingSpoof());
        this.forceRegister(new SkinBlink());
        this.forceRegister(new Spammer());
        this.forceRegister(new ToolTips());
        this.forceRegister(new TpsSync());
        this.forceRegister(new Tracker());
        this.forceRegister(new TrueDurability());
        this.forceRegister(new AutoCraft());
        this.forceRegister(new AutoRegear());
        this.forceRegister(new PacketDelay());
        this.forceRegister(new RPC());

        this.forceRegister(new Anchor());
        this.forceRegister(new AutoSprint());
        this.forceRegister(new BlockLag());
        this.forceRegister(new BoatFly());
        this.forceRegister(new ElytraFlight());
        this.forceRegister(new EntityControl());
        this.forceRegister(new EntitySpeed());
        this.forceRegister(new FastSwim());
        this.forceRegister(new Flight());
        this.forceRegister(new HighJump());
        this.forceRegister(new ReverseStep());
        this.forceRegister(new IceSpeed());
        this.forceRegister(new Jesus());
        this.forceRegister(new LongJump());
        this.forceRegister(new NoFall());
        this.forceRegister(new NoMove());
        this.forceRegister(new NoSlowDown());
        this.forceRegister(new PacketFly());
        this.forceRegister(new Phase());
        this.forceRegister(new SafeWalk());
        this.forceRegister(new Speed());
        this.forceRegister(new Step());
        this.forceRegister(new TickShift());
        this.forceRegister(new Velocity());

        this.forceRegister(new AutoMine());
        this.forceRegister(new AutoTool());
        this.forceRegister(new Blink());
        this.forceRegister(new BlockTweaks());
        this.forceRegister(new Cleaner());
        this.forceRegister(new ExpTweaks());
        this.forceRegister(new FakePlayer());
        this.forceRegister(new FastPlace());
        this.forceRegister(new FastEat());
        this.forceRegister(new Freecam());
        this.forceRegister(new LiquidInteract());
        this.forceRegister(new MiddleClickPearl());
        this.forceRegister(new MultiTask());
        this.forceRegister(new NCPTweaks());
        this.forceRegister(new NoGlitchBlocks());
        this.forceRegister(new NoHunger());
        this.forceRegister(new InventorySync());
        this.forceRegister(new NoRotate());
        this.forceRegister(new Arrows());
        this.forceRegister(new RayTrace());
        this.forceRegister(new Reach());
        this.forceRegister(new Replenish());
        this.forceRegister(new Scaffold());
        this.forceRegister(new Sorter());
        this.forceRegister(new Spectate());
        this.forceRegister(new Speedmine());
        this.forceRegister(new Suicide());
        this.forceRegister(new Timer());
        this.forceRegister(new XCarry());

        this.forceRegister(new BlockHighlight());
        this.forceRegister(new BreadCrumbs());
        this.forceRegister(new Chams());
        this.forceRegister(new ESP());
        this.forceRegister(new Fullbright());
        this.forceRegister(new HoleESP());
        this.forceRegister(new LagOMeter());
        this.forceRegister(new LogoutSpots());
        this.forceRegister(new VoidESP());
        this.forceRegister(new Nametags());
        this.forceRegister(new NewChunks());
        this.forceRegister(new NoRender());
        this.forceRegister(new Search());
        this.forceRegister(new Skeleton());
        this.forceRegister(new Sounds());
        this.forceRegister(new Tracers());
        this.forceRegister(new CameraClip());
        this.forceRegister(new ViewModel());
        this.forceRegister(new XRay());
        this.forceRegister(new CrystalScale());
        this.forceRegister(new Trails());
        this.forceRegister(new Trajectories());
        this.forceRegister(new Penis());
        this.forceRegister(new WayPoints());
        this.forceRegister(new Weather());
        this.forceRegister(new HandChams());
        this.forceRegister(new CrystalChams());
        this.forceRegister(new RainbowEnchant());
        this.forceRegister(new Crosshair());
        this.forceRegister(new PopChams());
        this.forceRegister(new ItemChams());
        this.forceRegister(new Ambience());

        this.forceRegister(new PingBypass());

        Bus.EVENT_BUS.post(new PostInitEvent());
    }

    public void load()
    {
        Caches.setManager(this);
        for (Module module : getRegistered())
        {
            module.load();
        }
    }

    @Override
    public void unregister(Module module) throws CantUnregisterException
    {
        super.unregister(module);
        Bus.EVENT_BUS.unsubscribe(module);
    }

    private void forceRegister(Module module)
    {
        registered.add(module);
        if (module instanceof Registrable)
        {
            ((Registrable) module).onRegister();
        }
    }

    public ArrayList<Module> getModulesFromCategory(Category moduleCategory) {
        final ArrayList<Module> iModules = new ArrayList<>();
        for (Module iModule : getRegistered()) {
            if (iModule.getCategory() == moduleCategory) iModules.add(iModule);
        }
        return iModules;
    }
}
