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
import java.util.Locale;
import java.util.Map;

/**
 * The Context of a Macro including Locale and the arguments of the Macro if any
 */
public class Context
{
    public static final Context EMPTY = new Context(Collections.<ContextProperty<?>, Object>emptyMap());

    private final Map<ContextProperty<?>, Object> properties;

    public static final ContextProperty<Locale> LOCALE = new ContextProperty<Locale>();

    private Context(Map<ContextProperty<?>, Object> properties)
    {
        this.properties = Collections.unmodifiableMap(properties);
    }

    /**
     * Returns the Locale
     * @return the locale
     */
    public Locale getLocale()
    {
        return getOrElse(LOCALE, Locale.getDefault());
    }

    public <K, V extends K> V get(ContextProperty<K> key)
    {
        return key.get(properties);
    }

    public <K, V extends K> V getOrElse(ContextProperty<K> key, V def)
    {
        return key.getOrElse(properties, def);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Context))
        {
            return false;
        }

        final Context context = (Context)o;

        return properties.equals(context.properties);
    }

    @Override
    public int hashCode()
    {
        return properties.hashCode();
    }

    /**
     * Replaces the list of arguments allowing to reuse the context
     * @param list the list to replace with
     * @return fluent interface
     */
    public Context with(Map<ContextProperty<?>, Object> properties)
    {
        return create(properties);
    }

    public static Context create()
    {
        return EMPTY;
    }

    public static Context create(Locale locale)
    {
        return create(LOCALE.to(locale));
    }

    public static Context create(Map<ContextProperty<?>, Object> properties)
    {
        if (properties.isEmpty())
        {
            return EMPTY;
        }
        return new Context(properties);
    }
}
