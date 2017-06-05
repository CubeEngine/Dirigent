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
package org.cubeengine.dirigent.context;

import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Context} class.
 */
public class ContextTest
{
    private final static TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    private final static TimeZone BERLIN_TIMEZONE = TimeZone.getTimeZone("Europe/Berlin");

    private final static Currency US_DOLLAR = Currency.getInstance(Locale.US);
    private final static Currency CNY = Currency.getInstance(Locale.CHINA);

    @Test
    public void testDefaultProviderOfLocale()
    {
        Locale.setDefault(Locale.FRANCE);
        Assert.assertEquals(Locale.FRANCE, Contexts.EMPTY.get(Contexts.LOCALE));

        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals(Locale.GERMANY, Contexts.EMPTY.get(Contexts.LOCALE));
    }

    @Test
    public void testDefaultProviderOfTimeZone()
    {
        TimeZone.setDefault(UTC_TIMEZONE);
        Assert.assertEquals(UTC_TIMEZONE, Contexts.EMPTY.get(Contexts.TIMEZONE));

        TimeZone.setDefault(BERLIN_TIMEZONE);
        Assert.assertEquals(BERLIN_TIMEZONE, Contexts.EMPTY.get(Contexts.TIMEZONE));
    }

    @Test
    public void testDefaultProviderOfCurrency()
    {
        Locale.setDefault(Locale.CHINA);
        Assert.assertEquals(CNY, Contexts.EMPTY.get(Contexts.CURRENCY));

        Locale.setDefault(Locale.US);
        Assert.assertEquals(US_DOLLAR, Contexts.EMPTY.get(Contexts.CURRENCY));
    }

    @Test
    public void testGetWithDefaultProvider()
    {
        Locale.setDefault(Locale.CHINA);
        Assert.assertEquals(Locale.GERMANY, Contexts.EMPTY.getOrElse(Contexts.LOCALE, new DefaultProvider<Locale>()
        {
            @Override
            public Locale defaultValue(Context context)
            {
                return Locale.GERMANY;
            }
        }));
    }

    @Test
    public void testGet()
    {
        Locale.setDefault(Locale.US);
        Context context = Contexts.createContext(Locale.CHINA);

        Assert.assertEquals(Locale.CHINA, context.get(Contexts.LOCALE));
    }

    @Test
    public void testGetWithContextPropertyWithDefaultDefaultProvider() {
        ContextProperty<String> contextProperty = new ContextProperty<String>();
        Assert.assertNull(Contexts.EMPTY.get(contextProperty));
    }

    @Test
    public void testSetSingleProperty()
    {
        Locale.setDefault(Locale.US);
        Context context = Contexts.createContext(Locale.US);

        Assert.assertEquals(Locale.US, context.get(Contexts.LOCALE));
        Assert.assertEquals(US_DOLLAR, context.get(Contexts.CURRENCY));

        Context newContext = context.set(Contexts.CURRENCY, CNY);

        Assert.assertEquals(Locale.US, newContext.get(Contexts.LOCALE));
        Assert.assertEquals(CNY, newContext.get(Contexts.CURRENCY));
    }

    @Test
    public void testSetPropertyVararg()
    {

        Locale.setDefault(Locale.US);
        TimeZone.setDefault(UTC_TIMEZONE);
        Context context = Contexts.createContext(Locale.US);

        Assert.assertEquals(Locale.US, context.get(Contexts.LOCALE));
        Assert.assertEquals(US_DOLLAR, context.get(Contexts.CURRENCY));
        Assert.assertEquals(UTC_TIMEZONE, context.get(Contexts.TIMEZONE));

        Context newContext = context.set(Contexts.CURRENCY.with(CNY), Contexts.TIMEZONE.with(BERLIN_TIMEZONE));

        Assert.assertEquals(Locale.US, newContext.get(Contexts.LOCALE));
        Assert.assertEquals(CNY, newContext.get(Contexts.CURRENCY));
        Assert.assertEquals(BERLIN_TIMEZONE, newContext.get(Contexts.TIMEZONE));
    }
}
