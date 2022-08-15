package me.earth.earthhack.impl.modules.client.anticheat;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.commands.ModuleCommand;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.client.SettingUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;

import java.util.function.BiConsumer;

public class AntiCheat extends Module {
    private static final SettingCache<ACRotate, Setting<ACRotate>, AutoCrystal>
        AC_ROTATE = Caches.getSetting(
            AutoCrystal.class, EnumSetting.class, "Rotate", ACRotate.None);
    private static final SettingCache<Boolean, Setting<Boolean>, KillAura>
        KILL_AURA_ROTATE = Caches.getSetting(
            KillAura.class, BooleanSetting.class, "Rotate", false);
    private static final SettingCache<Boolean, Setting<Boolean>, Scaffold>
        SCAFFOLD_ROTATE = Caches.getSetting(
        Scaffold.class, BooleanSetting.class, "Rotate", false);

    protected final Setting<Boolean> sync =
        register(new BooleanSetting("Sync", false));
    protected final Setting<Boolean> attackRotations =
        register(new BooleanSetting("Attack-Rotations", false));
    protected final Setting<Rotate> placeRotations =
        register(new EnumSetting<>("Place-Rotations", Rotate.None));
    protected final Setting<Boolean> newVer =
        register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities =
        register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> raytrace =
        register(new BooleanSetting("Raytrace", false));
    protected final Setting<Integer> cooldown =
        register(new NumberSetting<>("Switch-Cooldown", 0, 0, 500));
    protected final Setting<CooldownBypass> cooldownBypass =
        register(new EnumSetting<>("CooldownBypass", CooldownBypass.None));
    protected final Setting<Boolean> placeAttack =
        register(new BooleanSetting("Place-Attack", false));
    protected final Setting<Boolean> syncNow =
        register(new BooleanSetting("Sync-Now", false));
    protected final Setting<Boolean> message =
        register(new BooleanSetting("Message", true))
            .setComplexity(Complexity.Expert);

    public AntiCheat() {
        super("AntiCheat", Category.Client);
        SimpleData data = new SimpleData(
            this, "Configure certain settings globally.");
        data.register(sync, "While this setting is active all changes made" +
            " to settings in this module will be synced with all modules.");
        data.register(attackRotations, "Rotates when attacking Entities.");
        data.register(placeRotations, "Rotates when placing Blocks.");
        data.register(newVer, "Use on servers that are on Minecraft" +
            " version 1.13 and above. Takes into account that crystals can " +
            "be placed in one block tall spaces.");
        data.register(raytrace, "If the server requires you to raytrace.");
        data.register(cooldown, "How long to wait with attacking after" +
            " you switched your main hand item.");
        data.register(newVerEntities, "Only use on CrystalPvP.cc");
        data.register(syncNow, "Click to immediately sync all settings.");
        data.register(message,
                      "If you want to send a message when you set a setting.");
        data.register(placeAttack, "If modules such as Surround should" +
            " attack crystals to place.");
        this.setData(data);

        syncNow.addObserver(e -> {
            if (e.getValue()) {
                ModuleUtil.sendMessage(this, "Syncing all settings...");
                syncNow.setValue(true, false);
                attackRotations.notifyObservers(attackRotations.getValue());
                placeRotations.notifyObservers(placeRotations.getValue());
                newVer.notifyObservers(newVer.getValue());
                newVerEntities.notifyObservers(newVerEntities.getValue());
                raytrace.notifyObservers(raytrace.getValue());
                cooldown.notifyObservers(cooldown.getValue());
                cooldownBypass.notifyObservers(cooldownBypass.getValue());
                placeAttack.notifyObservers(placeAttack.getValue());
                e.setCancelled(true);
                syncNow.setValue(false, false);
                ModuleUtil.sendMessage(this, "Done syncing.");
            }
        });

        addIterateObserver(raytrace, (e, s) -> {
           if (s.getInitial() instanceof RayTraceMode) {
               if (e.getValue()) {
                   set(s, RayTraceMode.Smart);
               } else {
                   set(s, RayTraceMode.Fast);
               }
           } else if ("raytrace".equalsIgnoreCase(s.getName())
               && s.getInitial() instanceof Boolean) {
               set(s, e.getValue());
           }
        });

        addIterateObserver(cooldownBypass, (e, s) -> {
            if (s.getInitial() instanceof CooldownBypass) {
                set(s, e.getValue());
            }
        });

        addIterateObserver(placeAttack, (e, s) -> {
            if ((s.getContainer() instanceof ObbyModule
                || s.getContainer() instanceof BlockLag
                || s.getContainer() instanceof Scaffold)
                && "Attack".equals(s.getName())
                && s.getInitial() instanceof Boolean) {
                set(s, e.getValue());
            }
        });

        addIterateObserver(cooldown, (e, s) -> {
            if (s instanceof NumberSetting
                && "cooldown".equalsIgnoreCase(s.getName())
                && s.getInitial() instanceof Integer
                && ((NumberSetting<?>) s).getMin().intValue() == 0
                && ((NumberSetting<?>) s).getMax().intValue() == 500) {
                set(s, e.getValue());
            }
        });

        addSyncedObserver(placeRotations, e -> {
            Setting<Boolean> scaffoldRotate = SCAFFOLD_ROTATE.get();
            if (scaffoldRotate != null) {
                set(scaffoldRotate, e.getValue() != Rotate.None);
            }
        });

        addIterateObserver(placeRotations, (e, s) -> {
            if (s.getInitial() instanceof Rotate) {
                set(s, e.getValue());
            }
        });

        addIterateObserver(newVer, (e, s) -> {
            if (newVer.getName().equals(s.getName())
                    && s.getInitial() instanceof Boolean) {
                set(s, e.getValue());
            }
        });

        addIterateObserver(newVerEntities, (e, s) -> {
            if (newVerEntities.getName().equals(s.getName())
                && s.getInitial() instanceof Boolean) {
                set(s, e.getValue());
            }
        });

        addSyncedObserver(attackRotations, e -> {
            Setting<ACRotate> acRotate = AC_ROTATE.get();
            if (acRotate != null) {
                if (e.getValue()) {
                    switch (acRotate.getValue()) {
                        case Break:
                        case None:
                            set(acRotate, e.getValue()
                                ? ACRotate.Break : ACRotate.None);
                            break;
                        default:
                            set(acRotate,  e.getValue()
                                ? ACRotate.All : ACRotate.Place);
                            break;
                    }
                }
            }

            Setting<Boolean> killAuraRotate = KILL_AURA_ROTATE.get();
            if (killAuraRotate != null) {
                set(killAuraRotate, e.getValue());
            }
        });
    }

    private <T> void addIterateObserver(
        Setting<T> setting, BiConsumer<SettingEvent<T>, Setting<?>> consumer) {
        addSyncedObserver(setting, e -> SettingUtil.iterateAllSettings(
            s -> {
                if (s != setting) {
                    consumer.accept(e, s);
                }
            }));
    }

    private <T> void addSyncedObserver(Setting<T> setting,
                                       Observer<SettingEvent<T>> observer) {
        setting.addObserver(event -> {
            if (sync.getValue() || syncNow.getValue()) {
                observer.onChange(event);
            }
        });
    }

    private <T> void set(Setting<T> setting, Object object) {
        SettingUtil.setUnchecked(setting, object);
        Module module;
        if (message.getValue() && setting.getContainer() instanceof Module) {
            module = (Module) setting.getContainer();
            ModuleCommand.sendSettingMessage(module, setting, module.getName());
        }
    }

}
