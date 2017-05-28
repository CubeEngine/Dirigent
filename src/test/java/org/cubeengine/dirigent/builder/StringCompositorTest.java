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
package org.cubeengine.dirigent.builder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.CurrencyFormatter;
import org.cubeengine.dirigent.formatter.DateFormatter;
import org.cubeengine.dirigent.formatter.DateTimeFormatter;
import org.cubeengine.dirigent.formatter.IntegerFormatter;
import org.cubeengine.dirigent.formatter.NumberFormatter;
import org.cubeengine.dirigent.formatter.PercentFormatter;
import org.cubeengine.dirigent.formatter.PostProcessor;
import org.cubeengine.dirigent.formatter.StaticTextFormatter;
import org.cubeengine.dirigent.formatter.StringFormatter;
import org.cubeengine.dirigent.formatter.TimeFormatter;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.component.Component;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.cubeengine.dirigent.context.Contexts.createContext;
import static org.junit.Assert.assertEquals;

public class StringCompositorTest
{
    private BuilderDirigent<String, StringBuilder> compositor;

    @Before
    public void setUp() throws Exception
    {
        compositor = new StringBuilderDirigent();

        compositor.registerFormatter(new CurrencyFormatter());
        compositor.registerFormatter(new DateFormatter());
        compositor.registerFormatter(new DateTimeFormatter());
        compositor.registerFormatter(new IntegerFormatter());
        compositor.registerFormatter(new NumberFormatter());
        compositor.registerFormatter(new PercentFormatter());
        compositor.registerFormatter(new StaticTextFormatter());
        compositor.registerFormatter(new StringFormatter());
        compositor.registerFormatter(new TimeFormatter());

        compositor.registerFormatter(new SampleReflectedFormatter());
    }

    private String compose(String raw, Object... args)
    {
        return compositor.compose(createContext(Locale.GERMANY), raw, args);
    }

    @Test
    public void testPlainText() throws Exception
    {
        String msg = "This is a pure String message";
        assertEquals(msg, compose(msg));

        msg = "Another pure String \\with escape character";
        assertEquals(msg, compose(msg));

        msg = "";
        assertEquals(msg, compose(msg));
    }

    @Test
    public void testDefaultFormatterWithPositioning() throws Exception
    {
        final String msg = "message with default formatter";
        assertEquals(msg, compose("message with {} formatter", "default"));

        assertEquals(msg, compose("message with {} {}", "default", "formatter"));

        assertEquals(msg, compose("message with {1} {0}", "formatter", "default"));

        assertEquals(msg, compose("message {} {2} {}", "with", "formatter", "default"));
    }

    @Test
    public void testFormatterIntegration() throws Exception
    {
        assertEquals("msg: 42,00 €", compose("msg: {currency}", 42));
        assertEquals("msg: 42", compose("msg: {integer}", 42.38));
        assertEquals("msg: 42%", compose("msg: {percent}", 0.42));
        assertEquals("msg: 42,4", compose("msg: {number:format=#.0}", 42.356321));

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2017, Calendar.JANUARY, 3, 21, 18, 0);
        final Date date = calendar.getTime();
        assertEquals("msg: Dienstag, 3. Januar 2017", compose("msg: {date:full}", date));
        assertEquals("msg: 21:18", compose("msg: {time:short}", date));
        assertEquals("msg: Dienstag, 3. Januar 2017 21:18", compose("msg: {datetime:full:time=short}", date));
        assertEquals("msg: 03.01.2017 21:18:00", compose("msg: {datetime}", date));
        assertEquals("msg: 2017.01.03 21:18", compose("msg: {date:format=YYYY.MM.dd HH\\:mm}", date));

        assertEquals("msg: A STRING", compose("msg: {string:uppercase}", "a string"));
        assertEquals("msg: 42", compose("msg: {string}", 42));

        assertEquals("msg: static text", compose("msg: {text:static text}"));
    }

    @Test
    public void testReflectedFormatterIntegration() throws Exception
    {
        assertEquals("msg: [string]", compose("msg: {sample}", "string"));
        assertEquals("msg: 5.6", compose("msg: {sample}", 5.6d));
        assertEquals("msg: <42>", compose("msg: {reflected}", 42));
    }

    @Test
    public void testFormatterWithWrongParameter() throws Exception
    {
        assertEquals("msg: {{unresolved: number}}", compose("msg: {number}", "string"));
    }

    @Test
    public void testUnavailableFormatterName() throws Exception
    {
        assertEquals("msg: {{unresolved: blub}}", compose("msg: {blub}", "string"));
    }

    @Test
    public void testConstantFormatterPositioning() throws Exception
    {
        assertEquals("Test: 1 and static text with 2",
                     compose("Test: {number} and {text:static text} with {number}", 1, 2));
    }

    @Test
    public void testChangeDefaultFormatter() throws Exception
    {
        BuilderDirigent<String, StringBuilder> compositor = new StringBuilderDirigent(new ReverseStringFormatter());
        Assert.assertEquals("esrever", compositor.compose("{}", "reverse"));
    }

    @Test
    public void testPostProcessor() throws Exception
    {
        compositor.findFormatter(null, "").getFormatter().addPostProcessor(new PostProcessor()
        {
            public Component process(Component component, Context context, Arguments args)
            {
                if (component instanceof Text)
                {
                    return new Text(((Text)component).getText() + "!");
                }
                return component;
            }
        });

        assertEquals("Postprocessor will add an exclamationpoint!",
                     compose("Postprocessor will add an exclamationpoint{}", ""));

        compositor.addPostProcessor(new PostProcessor()
        {
            public Component process(Component component, Context context, Arguments args)
            {
                if (component instanceof Text)
                {
                    return new Text("#" + ((Text)component).getText() + "#");
                }
                return component;
            }
        });

        assertEquals("#sharp#", compose("sharp"));
    }

    @Test
    public void testInputPositioning() throws Exception
    {
        assertEquals("I am 1 test", compose("{} {string} {long} {}", "I", "am", 1L, "test"));
        assertEquals("Doubling echo echo", compose("{} {1} {}", "Doubling", "echo", "unimportant"));
        assertEquals("echo Doubling echo", compose("{0} {1} {}", "echo", "Doubling", "unimportant"));
    }
}
