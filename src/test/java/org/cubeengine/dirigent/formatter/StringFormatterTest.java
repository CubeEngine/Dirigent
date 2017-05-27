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
import org.cubeengine.dirigent.formatter.argument.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link StringFormatter}.
 */
public class StringFormatterTest
{
    private StringFormatter stringFormatter = new StringFormatter();

    @Test
    public void testFormat()
    {
        checkFormat("42", 42, Locale.GERMANY, null);
        checkFormat("someTHING", "someTHING", Locale.GERMANY, null);
        checkFormat("something", "someTHING", Locale.GERMANY, StringFormatter.LOWERCASE_FLAG);
        checkFormat("SOMETHING", "someTHING", Locale.GERMANY, StringFormatter.UPPERCASE_FLAG);
    }

    private void checkFormat(final String expected, final Object object, final Locale locale, final String flag)
    {
        final Arguments arguments;
        if (flag == null)
        {
            arguments = Arguments.NONE;
        }
        else
        {
            arguments = Arguments.create(new Value(flag));
        }

        final Component component = stringFormatter.format(object, Context.create(locale), arguments);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getString());
    }
}
