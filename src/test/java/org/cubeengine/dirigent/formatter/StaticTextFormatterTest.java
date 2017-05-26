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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link StaticTextFormatter}.
 */
public class StaticTextFormatterTest
{
    private StaticTextFormatter staticTextFormatter = new StaticTextFormatter();

    @Test
    public void testFormat()
    {
        checkFormat(null, Locale.GERMANY, null);
        checkFormat("some Text", Locale.GERMANY, "some Text");
    }

    private void checkFormat(final String expected, final Locale locale, final String argument)
    {
        final List<Argument> arguments;
        if (argument == null)
        {
            arguments = Collections.emptyList();
        }
        else
        {
            arguments = Collections.<Argument>singletonList(new Value(argument));
        }

        final Context context = new Context(locale).with(arguments);
        final Component component = staticTextFormatter.format(context);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getString());
    }
}
