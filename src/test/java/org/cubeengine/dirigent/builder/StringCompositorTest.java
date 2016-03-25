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
package org.cubeengine.dirigent.builder;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.formatter.Context;
import org.cubeengine.dirigent.formatter.MirrorFormatter;
import org.cubeengine.dirigent.formatter.PostProcessor;
import org.cubeengine.dirigent.formatter.example.DateFormatter;
import org.cubeengine.dirigent.formatter.example.DecimalFormatter;
import org.cubeengine.dirigent.formatter.example.IntegerFormatter;
import org.cubeengine.dirigent.parser.component.Text;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringCompositorTest
{
    private BuilderDirigent<String, StringBuilder> compositor;

    @Before
    public void setUp() throws Exception
    {
        compositor = new StringBuilderDirigent();

        compositor.registerFormatter(new DateFormatter());
        compositor.registerFormatter(new IntegerFormatter());
        compositor.registerFormatter(new DecimalFormatter());
        compositor.registerFormatter(new MirrorFormatter("mirror"));
    }

    private String compose(String raw, Object... args)
    {
        return compositor.compose(Locale.GERMAN, raw, args);
    }

    @Test
    public void testString() throws Exception
    {
        String msg = "This is a pure String message";
        assertEquals(msg, compose(msg));

        msg = "Another pure String \\with escape character";
        assertEquals(msg, compose(msg));

        msg = "This is a {} String message";
        assertEquals(msg, compose(msg, "{}"));

        msg = "This is a {1}{} String message";
        assertEquals(msg, compose(msg, "{}", "{1}"));

        assertEquals("This is a cool String message", compose("This is a {} String message", "cool"));
    }

    @Test
    public void testLabelEscaping() {
        assertEquals("This is a 42.0 message.", this.compositor.compose(Locale.US, "This is a {decimal#test\\:2:1} message.", 42d));
    }

    @Test
    public void testNumbers()
    {
        assertEquals("This is a 42 message", compose("This is a {number} message", 42));
        assertEquals("Numbers: 1 2 3", compose("Numbers: {number} {2:number} {number}", 1, 3, 2));
        assertEquals("Decimal: 4,321 9,88 5,43210", compose("Decimal: {decimal} {2:decimal:2} {decimal:5}", 4.321, 5.4321, 9.87654321));
        assertEquals("Decimal: 4,321 9,88 5,43210", compose("Decimal: {decimal} {2:decimal:2} {decimal:5}", 4.321f, 5.4321f, BigDecimal.valueOf(9.87654321)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecimalFail() throws Exception
    {
        compose("Decimal: {decimal:r}", 4.5);
    }

    @Test
    public void testDates()
    {
        Calendar instance = Calendar.getInstance();
        instance.set(2014, Calendar.AUGUST, 1, 1, 0, 0);
        Date date = new Date(instance.getTimeInMillis());
        assertEquals("Year: 2014", compose("Year: {date:format=yyyy}", date));
        assertEquals("Date is: 2014-08-01", compose("Date is: {date:format=yyyy-MM-dd}", date));
        assertEquals("Date is: 2014-08-01 01:00:00", compose("Date is: {date:format=yyyy-MM-dd HH\\:mm\\:ss}", date));
        assertEquals("No Args: 01.08.14 01:00", compose("No Args: {date#will use default short conversion}", date));
    }

    @Test
    public void testConstantFormatter() throws Exception
    {
        final String msg = "Use of a mirror {mirror:flag:param=test:par2=42:t}";
        assertEquals(msg, compose(msg));

        assertEquals("Test: 1 and {mirror:4} with 2", compose("Test: {number} and {mirror:4} with {number}", 1, 2));
    }

    @Test
    public void testPostProcessor() throws Exception
    {
        compositor.findFormatter(null, "").addPostProcessor(new PostProcessor()
        {
            public Component process(Component component, Context context)
            {
                if (component instanceof Text)
                {
                    return new Text(((Text)component).getString() + "!");
                }
                return component;
            }
        });

        assertEquals("Postprocessor will add an exclamationpoint!", compose(
            "Postprocessor will add an exclamationpoint{}", ""));

        compositor.addPostProcessor(new PostProcessor()
        {
            public Component process(Component component, Context context)
            {
                if (component instanceof Text)
                {
                    return new Text("#" + ((Text)component).getString() + "#");
                }
                return component;
            }
        });

        assertEquals("#sharp#", compose("sharp"));
    }
}
