/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
