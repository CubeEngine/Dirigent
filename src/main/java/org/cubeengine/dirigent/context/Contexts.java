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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Useful helpers to work with contexts.
 */
public class Contexts
{
    /**
     * A reference of a simple empty context.
     */
    public static final Context EMPTY = new Context(Collections.<ContextProperty<?>, Object>emptyMap());

    /**
     * This property identifies a {@link Locale} instance in a context.
     */
    public static final ContextProperty<Locale> LOCALE = new ContextProperty<Locale>(new DefaultProvider<Locale>()
    {
        @Override
        public Locale defaultValue(Context context)
        {
            return Locale.getDefault();
        }
    });

    /**
     * This property identifies a {@link TimeZone} instance in a context.
     */
    public static final ContextProperty<TimeZone> TIMEZONE = new ContextProperty<TimeZone>(
        new DefaultProvider<TimeZone>()
        {
            @Override
            public TimeZone defaultValue(Context context)
            {
                return TimeZone.getDefault();
            }
        });

    /**
     * This property identifies a {@link Currency} instance in a context.
     */
    public static final ContextProperty<Currency> CURRENCY = new ContextProperty<Currency>(
        new DefaultProvider<Currency>()
        {
            @Override
            public Currency defaultValue(Context context)
            {
                return Currency.getInstance(context.get(LOCALE));
            }
        });

    /**
     * Creates a new empty context.
     *
     * @return an empty context.
     */
    public static Context createContext()
    {
        return EMPTY;
    }

    /**
     * Creates a new context holding the specified {@link Locale}.
     *
     * @param locale The locale of this context.
     *
     * @return the context.
     */
    public static Context createContext(Locale locale)
    {
        return createContext(locale, TimeZone.getDefault());
    }

    /**
     * Creates a new context holding the specified {@link Locale}.
     *
     * @param locale The locale for this context
     * @param timeZone the timezone for this context
     *
     * @return the context.
     */
    public static Context createContext(Locale locale, TimeZone timeZone)
    {
        return createContext(LOCALE.with(locale), TIMEZONE.with(timeZone), CURRENCY.with(Currency.getInstance(locale)));
    }

    /**
     * Creates a new context with the given property mappings.
     *
     * @param mappings The property mappings for the context
     *
     * @return the context
     */
    public static Context createContext(PropertyMapping<?>... mappings)
    {
        return createContext(Arrays.asList(mappings));
    }

    /**
     * Creates a new context with the given property mappings.
     *
     * @param mappings The property mappings for the context
     *
     * @return the context
     */
    public static Context createContext(Collection<PropertyMapping<?>> mappings)
    {
        Map<ContextProperty<?>, Object> properties = new HashMap<ContextProperty<?>, Object>(mappings.size());
        for (final PropertyMapping<?> mapping : mappings)
        {
            properties.put(mapping.property, mapping.value);
        }
        return new Context(properties);
    }
}
