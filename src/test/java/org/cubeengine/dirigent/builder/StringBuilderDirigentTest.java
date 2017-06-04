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
import org.cubeengine.dirigent.Dirigent;
import org.cubeengine.dirigent.context.Contexts;
import org.cubeengine.dirigent.formatter.CurrencyFormatter;
import org.cubeengine.dirigent.formatter.DateFormatter;
import org.cubeengine.dirigent.formatter.DateTimeFormatter;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.IntegerFormatter;
import org.cubeengine.dirigent.formatter.NumberFormatter;
import org.cubeengine.dirigent.formatter.PercentFormatter;
import org.cubeengine.dirigent.formatter.StaticTextFormatter;
import org.cubeengine.dirigent.formatter.StringFormatter;
import org.cubeengine.dirigent.formatter.TimeFormatter;
import org.cubeengine.dirigent.formatter.WrappingPostProcessor;
import org.cubeengine.dirigent.parser.MacroResolutionResult;
import org.cubeengine.dirigent.parser.MacroResolutionState;
import org.cubeengine.dirigent.parser.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.cubeengine.dirigent.context.Contexts.createContext;
import static org.junit.Assert.assertEquals;

public class StringBuilderDirigentTest
{
    private StringBuilderDirigent dirigent;

    @Before
    public void setUp() throws Exception
    {
        dirigent = new StringBuilderDirigent();

        dirigent.registerFormatter(new CurrencyFormatter());
        dirigent.registerFormatter(new DateFormatter());
        dirigent.registerFormatter(new DateTimeFormatter());
        dirigent.registerFormatter(new IntegerFormatter());
        dirigent.registerFormatter(new NumberFormatter());
        dirigent.registerFormatter(new PercentFormatter());
        dirigent.registerFormatter(new StaticTextFormatter());
        dirigent.registerFormatter(new StringFormatter());
        dirigent.registerFormatter(new TimeFormatter());

        dirigent.registerFormatter(new SampleReflectedFormatter());
    }

    private String compose(String raw, Object... args)
    {
        return dirigent.compose(createContext(Locale.GERMANY), raw, args);
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
    public void testNumberFormatterIntegration() throws Exception
    {
        assertEquals("msg: 42,00 €", compose("msg: {currency}", 42));
        assertEquals("msg: 42,00 €", compose("msg: {money}", 42));
        assertEquals("msg: 42,00 €", compose("msg: {finance}", 42));

        assertEquals("msg: 42", compose("msg: {integer}", 42.38));
        assertEquals("msg: 42", compose("msg: {long}", 42.38));
        assertEquals("msg: 42", compose("msg: {short}", 42.38));
        assertEquals("msg: 42", compose("msg: {amount}", 42.38));
        assertEquals("msg: 42", compose("msg: {count}", 42.38));

        assertEquals("msg: 42%", compose("msg: {percent}", 0.42));
        assertEquals("msg: 42%", compose("msg: {percentage}", 0.42));

        assertEquals("msg: 42,4", compose("msg: {number:format=#.0}", 42.356321));
        assertEquals("msg: 42,4", compose("msg: {decimal}", 42.4));
        assertEquals("msg: 42,4", compose("msg: {double}", 42.4));
        assertEquals("msg: 42,4", compose("msg: {float}", 42.4));
    }

    @Test
    public void testDateTimeFormatterIntegraion() throws Exception
    {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2017, Calendar.JANUARY, 3, 21, 18, 0);
        final Date date = calendar.getTime();
        assertEquals("msg: Dienstag, 3. Januar 2017", compose("msg: {date:full}", date));
        assertEquals("msg: 21:18", compose("msg: {time:short}", date));
        assertEquals("msg: Dienstag, 3. Januar 2017 21:18", compose("msg: {datetime:full:time=short}", date));
        assertEquals("msg: 03.01.2017 21:18:00", compose("msg: {datetime}", date));
        assertEquals("msg: 2017.01.03 21:18", compose("msg: {date:format=YYYY.MM.dd HH\\:mm}", date));
    }

    @Test
    public void testStringFormatterIntegration() throws Exception
    {
        assertEquals("msg: A STRING", compose("msg: {string:uppercase}", "a string"));
        assertEquals("msg: 42", compose("msg: {string}", 42));
    }

    @Test
    public void testStaticTextFormatterIntegration() throws Exception
    {
        assertEquals("msg: static text", compose("msg: {text:static text}"));
    }

    @Test
    public void testNullInput() throws Exception
    {
        assertEquals("msg: {{unresolved: number}}", compose("msg: {number}", (Double)null));
        assertEquals("msg: null", compose("msg: {}", (Double)null));
    }

    @Test
    public void testReflectedFormatterIntegration() throws Exception
    {
        assertEquals("msg: [string]", compose("msg: {sample}", "string"));
        assertEquals("msg: 5.6", compose("msg: {sample}", 5.6d));
        assertEquals("msg: <42>", compose("msg: {reflected}", 42));
    }

    @Test
    public void testReflectedFormatterUnapplicableParameterType() throws Exception
    {
        assertEquals("msg: {{unresolved: reflected}}", compose("msg: {reflected}", new Date()));
    }

    @Test
    public void testReflectedFormatterNullInput() throws Exception
    {
        assertEquals("msg: {{unresolved: reflected}}", compose("msg: {reflected}", (String)null));
    }

    @Test
    public void testFormatterUnapplicableParameterType() throws Exception
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
                     compose("Test: {number} and {text:static text} with {number}", 1, 2, 3));
    }

    @Test
    public void testChangeDefaultFormatter() throws Exception
    {
        StringBuilderDirigent dirigent = new StringBuilderDirigent(new ReverseStringFormatter());
        Assert.assertEquals("esrever", dirigent.compose("{}", "reverse"));
    }

    @Test
    public void testNullDefaultFormatter() throws Exception
    {
        StringBuilderDirigent dirigent = new StringBuilderDirigent(null);
        Assert.assertEquals("{{unresolved}}", dirigent.compose("{}", "reverse"));
    }

    @Test(expected = IllegalStateException.class)
    public void testReflectedFormatterAsDefaultFormatterWhichDoesNotHandleTheInputType() throws Exception
    {
        StringBuilderDirigent dirigent = new StringBuilderDirigent(new SampleReflectedFormatter());
        dirigent.compose("{}", new Date());
    }

    @Test
    public void testFluentApi()
    {
        Dirigent<String> dirigent = new StringBuilderDirigent();
        Assert.assertEquals(dirigent, dirigent.registerFormatter(new NumberFormatter()).registerFormatter(
            new DateTimeFormatter()).addPostProcessor(new WrappingPostProcessor("[", "]")));
    }

    @Test
    public void testPostProcessorAtDirigentLevel() throws Exception
    {
        Dirigent<String> dirigent = new StringBuilderDirigent().addPostProcessor(new WrappingPostProcessor("[", "]"));
        Assert.assertEquals("[some text ][macro][ some text]", dirigent.compose("some text {} some text", "macro"));
    }

    @Test
    public void testPostProcessorAtFormatterLevel() throws Exception
    {
        Formatter<?> integerFormatter = new IntegerFormatter().addPostProcessor(new WrappingPostProcessor("<", ">"));
        Formatter<?> staticTextFormatter = new StaticTextFormatter().addPostProcessor(
            new WrappingPostProcessor(Text.EMPTY, Text.create("(!)")));

        Dirigent<String> dirigent = new StringBuilderDirigent().registerFormatter(integerFormatter).registerFormatter(
            staticTextFormatter).registerFormatter(new NumberFormatter());

        Assert.assertEquals("some text <42>, 42.435 and some static text(!).",
                            dirigent.compose(Contexts.createContext(Locale.US),
                                             "some text {integer}, {0:number} and some {text:static text}.", 42.43522));
    }

    @Test
    public void testInputPositioningDefaultMacro() throws Exception
    {
        assertEquals("I am 1 test", compose("{} {string} {long} {}", "I", "am", 1L, "test"));
        assertEquals("Doubling echo echo", compose("{} {1} {}", "Doubling", "echo", "unimportant"));
        assertEquals("echo Doubling echo", compose("{0} {1} {}", "echo", "Doubling", "unimportant"));
    }

    @Test
    public void testInputPositioningNamedMacro() throws Exception
    {
        assertEquals("echo Doubling echo",
                     compose("{0:string} {1:string} {string}", "echo", "Doubling", "unimportant"));
    }

    @Test
    public void testIllegalMacro() throws Exception
    {
        assertEquals("illegal macro {illegal", compose("illegal macro {illegal"));
    }

    @Test
    public void testFindFormatterOk() throws Exception
    {
        MacroResolutionResult resolutionResult = dirigent.findFormatter("number", 42);
        Assert.assertNotNull(resolutionResult);
        Assert.assertEquals(MacroResolutionState.OK, resolutionResult.getState());
        Assert.assertEquals(NumberFormatter.class, resolutionResult.getFormatter().getClass());
    }

    @Test
    public void testFindFormatterNonApplicable() throws Exception
    {
        MacroResolutionResult resolutionResult = dirigent.findFormatter("number", "string");
        Assert.assertNotNull(resolutionResult);
        Assert.assertEquals(MacroResolutionState.NONE_APPLICABLE, resolutionResult.getState());
        Assert.assertNull(resolutionResult.getFormatter());
    }

    @Test
    public void testFindFormatterUnknownName() throws Exception
    {
        MacroResolutionResult resolutionResult = dirigent.findFormatter("asdfg", "string");
        Assert.assertNotNull(resolutionResult);
        Assert.assertEquals(MacroResolutionState.UNKNOWN_NAME, resolutionResult.getState());
        Assert.assertNull(resolutionResult.getFormatter());
    }
}
