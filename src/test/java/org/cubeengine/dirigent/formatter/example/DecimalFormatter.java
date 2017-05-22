/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.dirigent.formatter.example;

import java.math.BigDecimal;
import java.text.NumberFormat;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.formatter.Context;
import org.cubeengine.dirigent.formatter.reflected.Format;
import org.cubeengine.dirigent.formatter.reflected.Names;
import org.cubeengine.dirigent.formatter.reflected.ReflectedFormatter;
import org.cubeengine.dirigent.parser.component.Text;

import static java.text.NumberFormat.getInstance;

/**
 * Formats {@link Float}, {@link Double} and {@link BigDecimal}s
 */
@Names("decimal")
public class DecimalFormatter extends ReflectedFormatter
{
    @Format
    public Component format(Float f, Context context)
    {
        return this.formatNumber(f, context);
    }

    @Format
    public Component format(Double d, Context context)
    {
        return this.formatNumber(d, context);
    }

    @Format
    public Component format(BigDecimal bigDecimal, Context context)
    {
        return this.formatNumber(bigDecimal, context);
    }

    protected Component formatNumber(Number number, Context context)
    {
        String arg = context.getFlag(0);
        NumberFormat decimalFormat = getInstance(context.getLocale());
        if (arg == null)
        {
            // no precision
            return new Text(decimalFormat.format(number));
        }
        else
        {
            try
            {
                final String[] parts = arg.split(":", 2);
                final Integer decimalPlaces;
                if (parts.length == 1)
                {
                    decimalPlaces = Integer.valueOf(parts[0]);
                }
                else
                {
                    final Integer integerDigits = Integer.valueOf(parts[0]);
                    decimalPlaces = Integer.valueOf(parts[1]);

                    decimalFormat.setMinimumIntegerDigits(integerDigits);
                }

                decimalFormat.setMaximumFractionDigits(decimalPlaces);
                decimalFormat.setMinimumFractionDigits(decimalPlaces);
                return new Text(decimalFormat.format(number));
            }
            catch (final NumberFormatException e)
            {
                throw new IllegalArgumentException(
                    "The 'decimal' type does not allow arguments other than integer for decimal places", e);
            }
        }
    }
}
