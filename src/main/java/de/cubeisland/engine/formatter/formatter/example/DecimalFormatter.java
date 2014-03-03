package de.cubeisland.engine.formatter.formatter.example;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.cubeisland.engine.formatter.context.FormatContext;
import de.cubeisland.engine.formatter.formatter.ReflectedFormatter;
import de.cubeisland.engine.formatter.formatter.reflected.Format;
import de.cubeisland.engine.formatter.formatter.reflected.Names;

@Names("decimal")
public class DecimalFormatter extends ReflectedFormatter
{
    @Format(Float.class)
    public String format(Float aFloat, FormatContext context)
    {
        return this.formatNumber(aFloat, context);
    }

    @Format(Double.class)
    public String format(Double aDouble, FormatContext context)
    {
        return this.formatNumber(aDouble, context);
    }

    @Format(BigDecimal.class)
    public String format(BigDecimal aBigDecimal, FormatContext context)
    {
        return this.formatNumber(aBigDecimal, context);
    }

    private String formatNumber(Number number, FormatContext context)
    {
        String arg = context.getArg(0);
        NumberFormat decimalFormat = DecimalFormat.getInstance(context.getLocale());
        if (arg == null) // No precision
        {
            return decimalFormat.format(number);
        }
        else
        {
            try
            {
                Integer decimalPlaces = Integer.valueOf(arg);
                decimalFormat.setMaximumFractionDigits(decimalPlaces);
                decimalFormat.setMinimumFractionDigits(decimalPlaces);
                return decimalFormat.format(number);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("The 'decimal' type does not allow arguments other than integer for decimal places");
            }
        }
    }
}
