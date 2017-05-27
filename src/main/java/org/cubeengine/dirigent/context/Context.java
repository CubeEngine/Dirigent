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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.cubeengine.dirigent.formatter.Formatter;

/**
 * This class specifies the context of a compose process which is triggered by the
 * {@link org.cubeengine.dirigent.Dirigent#compose(Context, String, Object...)} method. This might include a
 * {@link Locale} or other information which can be used by the formatters to build proper messages.
 */
public class Context
{
    /**
     * A reference of a simple empty context.
     */
    public static final Context EMPTY = new Context(Collections.<ContextProperty<?>, Object>emptyMap());

    /**
     * Identification context property for a {@link Locale} which must be considered within the {@link Formatter}
     * implementations.
     */
    public static final ContextProperty<Locale> LOCALE = new ContextProperty<Locale>();

    /**
     * A map holding all properties of the context.
     */
    private final Map<ContextProperty<?>, Object> properties;

    /**
     * Constructor.
     *
     * @param properties The properties of this context.
     */
    private Context(Map<ContextProperty<?>, Object> properties)
    {
        this.properties = Collections.unmodifiableMap(properties);
    }

    /**
     * Returns the Locale which must be considered from the {@link Formatter} implementations.
     *
     * @return the locale or the default locale if the property doesn't exist.
     */
    public Locale getLocale()
    {
        return getOrElse(LOCALE, Locale.getDefault());
    }

    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     *
     * @param key The context property.
     * @param <K> The type which is identified by the property.
     * @param <V> The actual value type.
     *
     * @return the value of the context property.
     */
    public <K, V extends K> V get(ContextProperty<K> key)
    {
        return getOrElse(key, null);
    }

    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     * If the value isn't specified the provided default value will be returned.
     *
     * @param key The context property.
     * @param def The default value.
     * @param <K> The type which is identified by the property.
     * @param <V> The actual value type.
     *
     * @return the value of the context property or the default value if it's not specified.
     */
    public <K, V extends K> V getOrElse(ContextProperty<K> key, V def)
    {

        @SuppressWarnings("unchecked")
        V val = (V)properties.get(key);
        if (val == null)
        {
            return def;
        }
        return val;
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
     * Creates a new empty context.
     *
     * @return an empty context.
     */
    public static Context create()
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
    public static Context create(Locale locale)
    {
        return create(LOCALE.with(locale));
    }

    /**
     * Creates a new context with the specified properties.
     *
     * @param mappings The property mappings of the context.
     *
     * @return the context.
     */
    public static Context create(PropertyMapping<?>... mappings)
    {
        return create(Arrays.asList(mappings));
    }

    public static Context create(Collection<PropertyMapping<?>> mappings)
    {
        Map<ContextProperty<?>, Object> properties = new HashMap<ContextProperty<?>, Object>(mappings.size());
        for (final PropertyMapping<?> mapping : mappings)
        {
            properties.put(mapping.property, mapping.value);
        }
        return new Context(properties);
    }
}
