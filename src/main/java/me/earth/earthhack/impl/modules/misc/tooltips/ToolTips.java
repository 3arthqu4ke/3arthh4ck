package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.entity.ITileEntityShulkerBox;
import me.earth.earthhack.impl.core.mixins.block.ITileEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ToolTips extends Module
{
    public static final ResourceLocation SHULKER_GUI_TEXTURE =
            new ResourceLocation("textures/gui/container/shulker_box.png");
    public static final ResourceLocation MAP =
            new ResourceLocation("textures/map/map_background.png");

    protected final Setting<Boolean> shulkers   =
            register(new BooleanSetting("Shulkers", true));
    protected final Setting<Boolean> maps       =
            register(new BooleanSetting("Maps", true));
    protected final Setting<Boolean> shulkerSpy =
            register(new BooleanSetting("ShulkerSpy", true));
    protected final Setting<Boolean> own        =
            register(new BooleanSetting("Own", true));
    protected final Setting<Bind> peekBind      =
            register(new BindSetting("PeekBind",
                Bind.fromKey(mc.gameSettings.keyBindSneak.getKeyCode())));

    protected final Map<String, TimeStack> spiedPlayers =
            new ConcurrentHashMap<>();

    public ToolTips()
    {
        super("ToolTips", Category.Misc);
        this.listeners.add(new ListenerToolTip(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRender2D(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerPostToolTip(this));
        this.setData(new ToolTipsData(this));
    }

    public boolean drawShulkerToolTip(ItemStack stack,
                                      int x,
                                      int y,
                                      String name)
    {
        if (stack != null && stack.getItem() instanceof ItemShulkerBox)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10))
            {
                NBTTagCompound blockEntityTag =
                        tagCompound.getCompoundTag("BlockEntityTag");

                if (blockEntityTag.hasKey("Items", 9))
                {
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.color(1.f, 1.f, 1.f, 1.f);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO);

                    mc.getTextureManager().bindTexture(
                            ToolTips.SHULKER_GUI_TEXTURE);

                    Render2DUtil.drawTexturedRect(
                            x, y, 0, 0, 176, 16, 500);
                    Render2DUtil.drawTexturedRect(
                            x, y + 16, 0, 16, 176, 57, 500);
                    Render2DUtil.drawTexturedRect(
                            x, y + 70, 0, 160, 176, 8, 500);

                    GlStateManager.disableDepth();

                    Managers.TEXT.drawStringWithShadow(name == null
                                    ? stack.getDisplayName()
                                    : name,
                            x + 8,
                            y + 6,
                            0xffffffff);

                    GlStateManager.enableDepth();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableLighting();
                    NonNullList<ItemStack> nonNullList =
                            NonNullList.withSize(27, ItemStack.EMPTY);

                    ItemStackHelper.loadAllItems(blockEntityTag, nonNullList);

                    for (int i = 0; i < nonNullList.size(); i++)
                    {
                        int iX = x + (i % 9) * (18) + 8;
                        int iY = y + (i / 9) * (18) + 18;
                        ItemStack itemStack = nonNullList.get(i);
                        mc.getRenderItem()
                                .zLevel = 501;
                        mc.getRenderItem()
                                .renderItemAndEffectIntoGUI(itemStack, iX, iY);
                        mc.getRenderItem()
                                .renderItemOverlayIntoGUI(
                                        mc.fontRenderer, itemStack, iX, iY, null);
                        mc.getRenderItem().zLevel = 0.f;
                    }

                    GlStateManager.disableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.f, 1.f, 1.f, 1.0f);
                    return true;
                }
            }
        }

        return false;
    }

    public void displayInventory(ItemStack stack, String name)
    {
        try
        {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox) item;
            //noinspection ConstantConditions
            ((ITileEntity) entityBox).setBlockType(shulker.getBlock());
            entityBox.setWorld(mc.world);

            ItemStackHelper.loadAllItems(
                    stack.getTagCompound().getCompoundTag("BlockEntityTag"),
                    ((ITileEntityShulkerBox) entityBox).getItems());

            entityBox.readFromNBT(
                    stack.getTagCompound().getCompoundTag("BlockEntityTag"));

            entityBox.setCustomName(name == null
                    ? stack.getDisplayName()
                    : name);

            mc.player.displayGUIChest(entityBox);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ItemStack getStack(String name)
    {
        TimeStack stack = spiedPlayers.get(name.toLowerCase());
        if (stack != null)
        {
            return stack.getStack();
        }

        return null;
    }

    public Set<String> getPlayers()
    {
        return spiedPlayers.keySet();
    }

}

