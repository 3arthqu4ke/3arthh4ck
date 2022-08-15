package me.earth.earthhack.impl.gui.chat.components.values;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * A ValueComponent.
 *
 * These Components display the value of the given Setting.
 */
public class ValueComponent extends SuppliedComponent
{
    private final Setting<?> setting;

    public ValueComponent(Setting<?> setting)
    {
        super(() ->
        {
            if (setting.getValue() == null)
            {
                return "null";
            }

            if (setting instanceof StringSetting
                    && setting.getValue().toString().isEmpty())
            {
                return "<...>";
            }

            if (setting instanceof StringSetting
                && ((StringSetting) setting).isPassword())
            {
                return ((StringSetting) setting).censor();
            }

            return setting.getValue().toString();
        });

        this.setting = setting;
    }

    @Override
    public TextComponentString createCopy()
    {
        ValueComponent copy = new ValueComponent(setting);
        copy.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent sibling : this.getSiblings())
        {
            copy.appendSibling(sibling.createCopy());
        }

        return copy;
    }

}
