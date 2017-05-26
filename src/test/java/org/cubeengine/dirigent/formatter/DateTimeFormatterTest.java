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

import java.util.Date;
import java.util.Locale;
import org.junit.Test;

/**
 * Tests the {@link DateTimeFormatter}.
 */
public class DateTimeFormatterTest extends AbstractDateTimeFormatterTest
{
    public DateTimeFormatterTest()
    {
        super(new DateTimeFormatter());
    }

    @Test
    public void testFormat()
    {
        final Date date = createDate();

        checkFormat("25.05.2017 15:13:21", date, Locale.GERMANY, null);
        checkFormat("May 25, 2017 3:13:21 PM", date, Locale.US, null);

        checkFormat("Donnerstag, 25. Mai 2017 15:13 Uhr UTC", date, Locale.GERMANY, DateTimeFormatter.FULL_STYLE);
        checkFormat("Thursday, May 25, 2017 3:13:21 PM UTC", date, Locale.US, DateTimeFormatter.FULL_STYLE);

        checkFormat("25. Mai 2017 15:13:21 UTC", date, Locale.GERMANY, DateTimeFormatter.LONG_STYLE);
        checkFormat("May 25, 2017 3:13:21 PM UTC", date, Locale.US, DateTimeFormatter.LONG_STYLE);

        checkFormat("25.05.2017 15:13:21", date, Locale.GERMANY, DateTimeFormatter.MEDIUM_STYLE);
        checkFormat("May 25, 2017 3:13:21 PM", date, Locale.US, DateTimeFormatter.MEDIUM_STYLE);

        checkFormat("25.05.17 15:13", date, Locale.GERMANY, DateTimeFormatter.SHORT_STYLE);
        checkFormat("5/25/17 3:13 PM", date, Locale.US, DateTimeFormatter.SHORT_STYLE);
    }

    @Test
    public void testFormatIndividualStyle()
    {
        final Date date = createDate();

        checkFormat("25.05.17 15:13:21", date, Locale.GERMANY, DateTimeFormatter.FULL_STYLE,
                    DateTimeFormatter.SHORT_STYLE, DateTimeFormatter.MEDIUM_STYLE);
        checkFormat("Donnerstag, 25. Mai 2017 15:13", date, Locale.GERMANY, DateTimeFormatter.SHORT_STYLE,
                    DateTimeFormatter.FULL_STYLE, null);
        checkFormat("25.05.17 15:13 Uhr UTC", date, Locale.GERMANY, DateTimeFormatter.SHORT_STYLE, null,
                    DateTimeFormatter.FULL_STYLE);
    }

    @Test
    public void testFormatWithFormat()
    {
        final Date date = createDate();

        checkFormat("2017.05.25 15:13:21", date, "YYYY.MM.dd HH:mm:ss");
    }
}
