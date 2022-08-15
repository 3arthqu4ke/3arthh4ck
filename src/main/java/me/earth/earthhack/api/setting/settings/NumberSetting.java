package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;

import java.text.NumberFormat;
import java.text.ParseException;

@SuppressWarnings("unchecked")
public class NumberSetting<N extends Number> extends Setting<N>
{
    private final boolean restriction;
    private final String  description;
    private final boolean floating;
    private final N max;
    private final N min;

    public NumberSetting(String nameIn, N initialValue)
    {
        super(nameIn, initialValue);
        N[] minMax = getDefaultMinMax();
        this.min = minMax[0];
        this.max = minMax[1];
        this.restriction = false;
        this.description = generateOutPut();
        this.floating    = isDoubleOrFloat();
    }

    public NumberSetting(String nameIn, N initialValue, N min, N max)
    {
        super(nameIn, initialValue);
        this.min = min;
        this.max = max;
        this.restriction = true;
        this.description = generateOutPut();
        this.floating    = isDoubleOrFloat();
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(numberToValue(element.getAsNumber()));
    }

    @Override
    public Setting<N> copy() {
        return new NumberSetting<>(getName(), getInitial(), getMin(), getMax());
    }

    @Override
    public SettingResult fromString(String string)
    {
        if (string == null)
        {
            return new SettingResult(false, "Value was null.");
        }

        String noComma = string.replace(',', '.');

        try
        {
            Number parsed = NumberFormat.getInstance().parse(noComma);
            N result = numberToValue(parsed);
            if (result == null)
            {
                return new SettingResult(false,
                        "The numberToValue method returned null.");
            }

            if (inBounds(result))
            {
                this.setValue(result);
                return SettingResult.SUCCESSFUL;
            }
        }
        catch (ParseException | ClassCastException e)
        {
            e.printStackTrace();
            return new SettingResult(false, string + " could not be parsed.");
        }

        return new SettingResult(false, string
                + " is out of bounds (" + min + "-" + max + ")");
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return description;
        }

        return "";
    }

    @Override
    public void setValue(N value, boolean withEvent)
    {
        if (inBounds(value))
        {
            super.setValue(value, withEvent);
        }
    }

    public boolean inBounds(N value)
    {
        return !restriction
                || (!(value.doubleValue() < min.doubleValue())
                        && !(value.doubleValue() > max.doubleValue()));
    }

    public boolean hasRestriction()
    {
        return restriction;
    }

    public N getMax()
    {
        return max;
    }

    public N getMin()
    {
        return min;
    }

    public N numberToValue(Number number)
    {
        Class<? extends Number> type = this.initial.getClass();
        Object result = null;

        if (type == Integer.class)
        {
            result = number.intValue();
        }
        else if (type == Float.class)
        {
            result = number.floatValue();
        }
        else if (type == Double.class)
        {
            result = number.doubleValue();
        }
        else if (type == Short.class)
        {
            result = number.shortValue();
        }
        else if (type == Byte.class)
        {
            result = number.byteValue();
        }
        else if (type == Long.class)
        {
            result = number.longValue();
        }

        return (N) result;
    }

    public boolean isFloating()
    {
        return floating;
    }

    private N[] getDefaultMinMax()
    {
        Class<? extends Number> type = this.initial.getClass();
        Object[] result = new Object[2];

        if (type == Integer.class)
        {
            result[0] = Integer.MIN_VALUE;
            result[1] = Integer.MAX_VALUE;
        }
        else if (type == Float.class)
        {
            result[0] = Float.MIN_VALUE;
            result[1] = Float.MAX_VALUE;
        }
        else if (type == Double.class)
        {
            result[0] = Double.MIN_VALUE;
            result[1] = Double.MAX_VALUE;
        }
        else if (type == Short.class)
        {
            result[0] = Short.MIN_VALUE;
            result[1] = Short.MAX_VALUE;
        }
        else if (type == Byte.class)
        {
            result[0] = Byte.MIN_VALUE;
            result[1] = Byte.MAX_VALUE;
        }
        else
        {
            result[0] = Long.MIN_VALUE;
            result[1] = Long.MAX_VALUE;
        }

        return (N[]) new Number[]{(Number) result[0], (Number) result[1]};
    }

    private String generateOutPut()
    {
        if (restriction)
        {
            return "<" + min.toString() + " - " + max.toString() + ">";
        }
        else
        {
            return "<-5, 1.0, 10 ... 1337>";
        }
    }

    private boolean isDoubleOrFloat()
    {
        Class<?> clazz = initial.getClass();
        return clazz == Double.class || clazz == Float.class;
    }

}
