package me.earth.earthhack.impl.modules.render.voidesp;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.List;
import java.util.*;

public class VoidESP extends BlockESPModule
{
    protected final Setting<Integer> radius =
        register(new NumberSetting<>("Radius", 20, 0, 200));
    protected final Setting<Integer> holes =
        register(new NumberSetting<>("Holes", 500, 0, 1000));
    protected final Setting<Integer> update =
        register(new NumberSetting<>("Update", 500, 0, 10000));
    protected final Setting<Boolean> liveHoles =
        register(new BooleanSetting("Live-Holes", false));
    protected final Setting<Integer> maxY =
        register(new NumberSetting<>("Max-Y", 256, -256, 256));

    protected List<BlockPos> voidHoles = Collections.emptyList();
    protected final StopWatch timer = new StopWatch();

    public VoidESP()
    {
        super("VoidESP", Category.Render);
        this.listeners.add(new ListenerRender(this));

        this.color.setValue(new Color(255, 0, 0, 76));
        this.outline.setValue(new Color(255, 0, 0, 242));
        this.height.setValue(0.0f);

        SimpleData data = new SimpleData(this, "Renders void holes.");
        data.register(radius,
                "The radius in which holes will get rendered.");
        data.register(holes,
                "The maximum amount of holes to render.");
        data.register(color,
                "The color void holes will be rendered in.");
        data.register(update,
                "Time in milliseconds until Holes get updated again.");
        data.register(height,
                "If the Hole should be rendered flat or not.");
        data.register(liveHoles,
                "Saves some CPU but shouldn't matter at all.");
        data.register(maxY,
                "If your Y-Level is higher than this Holes won't be rendered.");
        this.setData(data);
    }

    protected void updateHoles()
    {
        if (timer.passed(update.getValue()))
        {
            Managers.THREAD.submit(() ->
            {
                BlockPos playerPos = PositionUtil.getPosition();
                int cx = playerPos.getX(),
                    cz = playerPos.getZ();
                int r = this.radius.getValue();
                int max = Sphere.getRadius(r);
                List<BlockPos> voidHolesIn = voidHoles.size() != 0
                                            ? new ArrayList<>(voidHoles.size())
                                            : new LinkedList<>();
                int holeAmount = 0;

                for (int j = 0; j < max; j++) {
                    Vec3i vec = Sphere.get(j);
                    if (vec.getY() != 0) {
                        continue;
                    }
                    BlockPos bp = new BlockPos(cx + vec.getX(), 0, cz + vec.getZ());
                    if (mc.world.getBlockState(bp).getBlock() != Blocks.BEDROCK) {
                        voidHolesIn.add(bp);
                        if (liveHoles.getValue() && ++holeAmount >= holes.getValue()) {
                            break;
                        }
                    }
                }

                voidHolesIn.sort(Comparator.comparingDouble(pos ->
                        mc.player.getDistanceSq(pos)));

                mc.addScheduledTask(() -> this.voidHoles = voidHolesIn);
            });

            timer.reset();
        }
    }

}
