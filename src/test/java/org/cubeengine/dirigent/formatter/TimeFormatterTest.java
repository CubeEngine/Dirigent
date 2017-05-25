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
public class TimeFormatterTest extends AbstractDateTimeFormatterTest
{
    public TimeFormatterTest()
    {
        super(new TimeFormatter());
    }

    @Test
    public void testFormat()
    {
        final Date date = createDate();

        checkFormat("15:13:21", date, Locale.GERMANY, null);
        checkFormat("3:13:21 PM", date, Locale.US, null);

        checkFormat("15:13 Uhr MESZ", date, Locale.GERMANY, TimeFormatter.FULL_STYLE);
        checkFormat("3:13:21 PM CEST", date, Locale.US, TimeFormatter.FULL_STYLE);

        checkFormat("15:13:21 MESZ", date, Locale.GERMANY, TimeFormatter.LONG_STYLE);
        checkFormat("3:13:21 PM CEST", date, Locale.US, TimeFormatter.LONG_STYLE);

        checkFormat("15:13:21", date, Locale.GERMANY, TimeFormatter.MEDIUM_STYLE);
        checkFormat("3:13:21 PM", date, Locale.US, TimeFormatter.MEDIUM_STYLE);

        checkFormat("15:13", date, Locale.GERMANY, TimeFormatter.SHORT_STYLE);
        checkFormat("3:13 PM", date, Locale.US, TimeFormatter.SHORT_STYLE);
    }
}
