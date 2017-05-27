/*
 * The MIT License
 * Copyright © 2013 Cube Island
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
package org.cubeengine.dirigent.formatter;

import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link NumberFormatter}.
 */
public class NumberFormatterTest
{
    private final NumberFormatter numberFormatter = new NumberFormatter();

    @Test
    public void testFormatWithFormat()
    {
        checkFormat("12.345,0", 12345, Locale.GERMANY, NumberFormatter.FORMAT_PARAM_NAME, "#,###.0");
        checkFormat("36,5", 36.4567, Locale.GERMANY, NumberFormatter.FORMAT_PARAM_NAME, "#,###.0");
        checkFormat("036,5", 36.4567, Locale.GERMANY, NumberFormatter.FORMAT_PARAM_NAME, ",000.0");
        checkFormat("12%", 0.12, Locale.GERMANY, NumberFormatter.FORMAT_PARAM_NAME, "##%");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatWithWrongFormat() {
        checkFormat("", 123, Locale.GERMANY, NumberFormatter.FORMAT_PARAM_NAME, ".,.,");
    }

    @Test
    public void testFormatInteger()
    {
        checkFormat("12.345", 12345, Locale.GERMANY, NumberFormatter.INTEGER_MODE_FLAG, null);
        checkFormat("12.345", 12345.12, Locale.GERMANY, NumberFormatter.INTEGER_MODE_FLAG, null);
        checkFormat("5", 5, Locale.GERMANY, NumberFormatter.INTEGER_MODE_FLAG, null);
    }

    @Test
    public void testFormatCurrency()
    {
        checkFormat("12.345,00 €", 12345, Locale.GERMANY, NumberFormatter.CURRENCY_MODE_FLAG, null);
        checkFormat("45,26 €", 45.258, Locale.GERMANY, NumberFormatter.CURRENCY_MODE_FLAG, null);
    }

    @Test
    public void testFormatPercent()
    {
        checkFormat("12%", 0.12, Locale.GERMANY, NumberFormatter.PERCENT_MODE_FLAG, null);
        checkFormat("1.200%", 12, Locale.GERMANY, NumberFormatter.PERCENT_MODE_FLAG, null);
    }

    @Test
    public void testFormatDefault()
    {
        checkFormat("12.345", 12345, Locale.GERMANY, null, null);
        checkFormat("12.345,344", 12345.3441, Locale.GERMANY, null, null);
    }

    private void checkFormat(final String expected, final Number number, final Locale locale, final String mode,
                             final String value)
    {
        final Context context = Context.create(locale);
        final Arguments args = args(mode, value);
        final Component component = numberFormatter.format(number, context, args);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getString());
    }

    private Arguments args(final String paramName, final String paramValue)
    {
        if (paramValue == null && paramName != null)
        {
            return Arguments.create(new Value(paramName));
        }
        else if (paramValue != null && paramName != null)
        {
            return Arguments.create(new Parameter(paramName, paramValue));
        }
        else
        {
            return Arguments.NONE;
        }
    }
}
