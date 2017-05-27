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
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link CurrencyFormatter}.
 */
public class CurrencyFormatterTest
{
    private CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    @Test
    public void testFormat()
    {
        checkFormat("12.345,00 €", 12345, Locale.GERMANY);
        checkFormat("$12,345.00", 12345, Locale.US);
        checkFormat("45,26 €", 45.258, Locale.GERMANY);
    }

    private void checkFormat(final String expected, final Number number, final Locale locale)
    {
        final Component component = currencyFormatter.format(number, Context.create(locale), Arguments.NONE);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getText());
    }
}
