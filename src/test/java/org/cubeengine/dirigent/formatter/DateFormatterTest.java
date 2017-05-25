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
 * Tests the {@link TimeFormatter}.
 */
public class DateFormatterTest extends AbstractDateTimeFormatterTest
{
    public DateFormatterTest()
    {
        super(new DateFormatter());
    }

    @Test
    public void testFormat()
    {
        final Date date = createDate();

        checkFormat("25.05.2017", date, Locale.GERMANY, null);
        checkFormat("May 25, 2017", date, Locale.US, null);

        checkFormat("Donnerstag, 25. Mai 2017", date, Locale.GERMANY, DateFormatter.FULL_STYLE);
        checkFormat("Thursday, May 25, 2017", date, Locale.US, DateFormatter.FULL_STYLE);

        checkFormat("25. Mai 2017", date, Locale.GERMANY, DateFormatter.LONG_STYLE);
        checkFormat("May 25, 2017", date, Locale.US, DateFormatter.LONG_STYLE);

        checkFormat("25.05.2017", date, Locale.GERMANY, DateFormatter.MEDIUM_STYLE);
        checkFormat("May 25, 2017", date, Locale.US, DateFormatter.MEDIUM_STYLE);

        checkFormat("25.05.17", date, Locale.GERMANY, DateFormatter.SHORT_STYLE);
        checkFormat("5/25/17", date, Locale.US, DateFormatter.SHORT_STYLE);
    }
}
