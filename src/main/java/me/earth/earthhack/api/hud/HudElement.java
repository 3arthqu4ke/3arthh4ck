package me.earth.earthhack.api.hud;

import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Hideable;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.gui.hud.rewrite.HudEditorGui;
import me.earth.earthhack.impl.gui.hud.rewrite.SnapPoint;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Vec2d;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * hud element
 * @author megyn
 */
public abstract class HudElement extends SettingContainer
        implements Globals, Subscriber, Nameable {

    private final Setting<Boolean> enabled =
            register(new BooleanSetting("Enabled", false));
    /**
     * These may not be settings in the future, so getting/settings their values will be done through wrapper methods.
     * These should really not be accessed directly for the time being.
     */
    private final Setting<Float> x =
            register(new NumberSetting<>("X", 2.0f, -2000.0f, 2000.0f));
    private final Setting<Float> y =
            register(new NumberSetting<>("Y", 2.0f, -2000.0f, 2000.0f));
    private final Setting<Integer> z =
            register(new NumberSetting<>("Z", 0, -2000, 2000)); // Z level determines rendering order.
    private final Setting<Float> scale =
            register(new NumberSetting<>("Scale", 1.0f, 0.1f, 10.0f));

    protected final List<Listener<?>> listeners = new ArrayList<>();
    private final AtomicBoolean enableCheck = new AtomicBoolean();
    private final AtomicBoolean inOnEnable  = new AtomicBoolean();
    private HudElement snappedTo;
    private SnapAxis axis = SnapAxis.NONE;

    private final String name; // Name will not be changeable for now, no point.

    // These have to be set appropriately, and ideally should be done in the constructor.
    private float width  = 100;
    private float height = 100;
    // private float scale  = 1.0f;

    // private final boolean scalable; // TODO: hud element scaling AFTER everything else works!
    // private boolean scaling = false;
    // private GuiUtil.Edge currentEdge;

    private boolean dragging;
    private float draggingX;
    private float draggingY;

    /**
     * Creates a new HudElement. It's important that the given name
     * does not contain any whitespaces and that no hud elements with the
     * same name exist. A hud element's name is its unique identifier.
     *
     * If this constructor is used, the width and height of the element must be set manually!
     *
     * @param name the name for the new hud element.
     */
    public HudElement(String name) {
        this.name = name;
        this.enabled.addObserver(event ->
        {
            if (event.isCancelled())
            {
                return;
            }

            enableCheck.set(event.getValue());
            if (event.getValue() && !Bus.EVENT_BUS.isSubscribed(this))
            {
                inOnEnable.set(true);
                onEnable();
                inOnEnable.set(false);
                if (enableCheck.get())
                {
                    Bus.EVENT_BUS.subscribe(this);
                }
            }
            else if (!event.getValue()
                    && (Bus.EVENT_BUS.isSubscribed(this) || inOnEnable.get()))
            {
                Bus.EVENT_BUS.unsubscribe(this);
                onDisable();
            }
        });
    }

    /**
     * Same as above :P
     *
     * @param name name of the hud element
     * @param x x of the element
     * @param y y of the element
     * @param width width of the element
     * @param height height of the element
     */
    public HudElement(String name, float x, float y, float width, float height) {
        this(name);
        this.x.setValue(x);
        this.y.setValue(y);
        this.width = width;
        this.height = height;
    }

    // TODO: maybe a bit of abstraction with these?
    public final void toggle()
    {
        if (isEnabled())
        {
            disable();
        }
        else
        {
            enable();
        }
    }

    public final void enable()
    {
        if (!isEnabled())
        {
            enabled.setValue(true);
        }
    }

    public final void disable()
    {
        if (isEnabled())
        {
            enabled.setValue(false);
        }
    }

    public final void load()
    {
        if (this.isEnabled() && !Bus.EVENT_BUS.isSubscribed(this))
        {
            Bus.EVENT_BUS.subscribe(this);
        }

        onLoad();
    }

    protected void onEnable()
    {
        /* Implemented by the module */
    }

    protected void onDisable()
    {
        /* Implemented by the module */
    }

    protected void onLoad()
    {
        /* Implemented by the module */
    }

    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            setX(mouseX - draggingX);
            setY(mouseY - draggingY);
        }
    }

    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        Render2DUtil.drawRect(x.getValue(), y.getValue(), x.getValue() + width, y.getValue() + height, new Color(40, 40, 40, 255).getRGB());
    }

    public void guiKeyPressed(char eventChar, int key) {}

    public void guiMouseClicked(int mouseX, int mouseY, int mouseButton) {
        // currentEdge = GuiUtil.getHoveredEdge(this, mouseX, mouseY, 5);
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            setDragging(true);
            draggingX = mouseX - getX();
            draggingY = mouseY - getY();
        }
    }

    public void guiMouseReleased(int mouseX, int mouseY, int mouseButton) {
        setDragging(false);
        // scaling = false;
    }

    public void hudUpdate(float partialTicks) {}

    public abstract void hudDraw(float partialTicks);

    public boolean isOverlapping(HudElement other) {
        double[] rec1 = new double[]{this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight()};
        double[] rec2 = new double[]{other.getX(), other.getY(), other.getX() + other.getWidth(), other.getY() + other.getHeight()};
        if (rec1[0] == rec1[2] || rec1[1] == rec1[3] ||
                rec2[0] == rec2[2] || rec2[1] == rec2[3]) {
            // the line cannot have positive overlap
            return false;
        }

        return !(rec1[2] <= rec2[0] ||   // left
                rec1[3] <= rec2[1] ||   // bottom
                rec1[0] >= rec2[2] ||   // right
                rec1[1] >= rec2[3]);    // top
    }

    @Override
    public Collection<Listener<?>> getListeners() {
        return this.listeners;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else if (o instanceof HudElement)
        {
            String name = this.name;
            return name != null && name.equals(((HudElement) o).name);
        }

        return false;
    }

    public float getX() {
        return x.getValue();
    }

    public void setX(float x) {
        ScaledResolution resolution = new ScaledResolution(mc);
        this.x.setValue(MathHelper.clamp(x, 0, resolution.getScaledWidth()));
    }

    public float getY() {
        return y.getValue();
    }

    public void setY(float y) {
        ScaledResolution resolution = new ScaledResolution(mc);
        this.y.setValue(MathHelper.clamp(y, 0, resolution.getScaledHeight()));
    }

    public float getZ() {
        return z.getValue();
    }

    public void setZ(int z) {
        this.z.setValue(z);
    }

    public float getScale() {
        return scale.getValue();
    }

    public void setScale(float scale) {
        this.scale.setValue(scale);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDraggingX() {
        return draggingX;
    }

    public void setDraggingX(float draggingX) {
        this.draggingX = draggingX;
    }

    public float getDraggingY() {
        return draggingY;
    }

    public void setDraggingY(float draggingY) {
        this.draggingY = draggingY;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
        if (!dragging) {
            for (HudElement element : Managers.ELEMENTS.getRegistered()
                    .stream()
                    .sorted(Comparator.comparing(HudElement::getZ)).collect(Collectors.toList())) {
                if (this.isOverlapping(element)) {
                    this.setSnappedTo(element);
                    if (this.getY() < element.getY() + element.getHeight() / 2) {
                        setAxis(SnapAxis.TOP);
                    } else {
                        setAxis(SnapAxis.BOTTOM);
                    }
                } else {
                    setAxis(SnapAxis.NONE);
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    public HudElement getSnappedTo() {
        return snappedTo;
    }

    public void setSnappedTo(HudElement snappedTo) {
        this.snappedTo = snappedTo;
    }

    public SnapAxis getAxis() {
        return axis;
    }

    public void setAxis(SnapAxis axis) {
        this.axis = axis;
    }
}
