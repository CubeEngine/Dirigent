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
package de.cubeisland.engine.messagecompositor.parser.formatter.example;

import java.math.BigDecimal;
import java.text.NumberFormat;
import de.cubeisland.engine.messagecompositor.parser.component.MessageComponent;
import de.cubeisland.engine.messagecompositor.parser.component.Text;
import de.cubeisland.engine.messagecompositor.parser.formatter.Context;
import de.cubeisland.engine.messagecompositor.parser.formatter.reflected.Format;
import de.cubeisland.engine.messagecompositor.parser.formatter.reflected.Names;
import de.cubeisland.engine.messagecompositor.parser.formatter.reflected.ReflectedFormatter;

import static java.text.NumberFormat.getInstance;

@Names("decimal")
public class DecimalFormatter extends ReflectedFormatter
{
    @Format
    public MessageComponent format(Float f, Context context)
    {
        return this.formatNumber(f, context);
    }

    @Format
    public MessageComponent format(Double d, Context context)
    {
        return this.formatNumber(d, context);
    }

    @Format
    public MessageComponent format(BigDecimal bigDecimal, Context context)
    {
        return this.formatNumber(bigDecimal, context);
    }

    protected MessageComponent formatNumber(Number number, Context context)
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
                Integer decimalPlaces = Integer.valueOf(arg);
                decimalFormat.setMaximumFractionDigits(decimalPlaces);
                decimalFormat.setMinimumFractionDigits(decimalPlaces);
                return new Text(decimalFormat.format(number));
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException(
                    "The 'decimal' type does not allow arguments other than integer for decimal places", e);
            }
        }
    }
}
