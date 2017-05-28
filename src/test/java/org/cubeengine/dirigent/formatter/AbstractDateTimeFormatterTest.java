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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.parser.Text;
import org.junit.Assert;

import static org.cubeengine.dirigent.context.Contexts.createContext;

/**
 * Abstract methods for tests of the {@link DateTimeFormatter}.
 */
public abstract class AbstractDateTimeFormatterTest
{
    private final DateTimeFormatter formatter;

    protected AbstractDateTimeFormatterTest(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    protected Date createDate()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        final Calendar calendar = GregorianCalendar.getInstance(Locale.US);
        calendar.set(2017, Calendar.MAY, 25, 15, 13, 21);
        return calendar.getTime();
    }

    protected void checkFormat(final String expected, final Date date, final Locale locale, final String style)
    {
        checkFormat(expected, date, locale, TimeZone.getDefault(), style, null, null, null);
    }

    protected void checkFormat(final String expected, final Date date, final Locale locale, final TimeZone timeZone,
                               final String style)
    {
        checkFormat(expected, date, locale, timeZone, style, null, null, null);
    }

    protected void checkFormat(final String expected, final Date date, final String format)
    {
        checkFormat(expected, date, Locale.US, TimeZone.getDefault(), null, null, null, format);
    }

    protected void checkFormat(final String expected, final Date date, final Locale locale, final String defaultStyle,
                               final String dateStyle, final String timeStyle)
    {
        checkFormat(expected, date, locale, TimeZone.getDefault(), defaultStyle, dateStyle, timeStyle, null);
    }

    private void checkFormat(final String expected, final Date date, final Locale locale, final TimeZone timeZone,
                             final String defaultStyle, final String dateStyle, final String timeStyle,
                             final String format)
    {
        final Context context = createContext(locale, timeZone);
        final Arguments args = createArguments(defaultStyle, dateStyle, timeStyle, format);

        final Component component = formatter.format(date, context, args);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getText());
    }

    private Arguments createArguments(final String defaultStyle, final String dateStyle, final String timeStyle,
                                      final String format)
    {
        final List<Argument> arguments = new LinkedList<Argument>();

        if (defaultStyle != null)
        {
            arguments.add(new Value(defaultStyle));
        }
        if (dateStyle != null)
        {
            arguments.add(new Parameter(DateTimeFormatter.DATE_PARAM_NAME, dateStyle));
        }
        if (timeStyle != null)
        {
            arguments.add(new Parameter(DateTimeFormatter.TIME_PARAM_NAME, timeStyle));
        }
        if (format != null)
        {
            arguments.add(new Parameter(DateTimeFormatter.FORMAT_PARAM_NAME, format));
        }

        return Arguments.create(arguments);
    }
}
