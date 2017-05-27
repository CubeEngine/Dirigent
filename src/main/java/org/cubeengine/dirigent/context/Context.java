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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * This class specifies the context of a compose process which is triggered by the
 * {@link org.cubeengine.dirigent.Dirigent#compose(Context, String, Object...)} method. This might include a
 * {@link Locale} or other information which can be used by the formatters to build proper messages.
 */
public class Context
{
    /**
     * A map holding all properties of the context.
     */
    private final Map<ContextProperty<?>, Object> properties;

    /**
     * Constructor.
     *
     * @param properties The properties of this context.
     */
    protected Context(Map<ContextProperty<?>, Object> properties)
    {
        this.properties = Collections.unmodifiableMap(properties);
    }

    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     *
     * @param key the context property
     * @param <K> the type which is identified by the property
     *
     * @return the value of the context property.
     */
    public <K> K get(ContextProperty<K> key)
    {
        return getOrElse(key, key.getDefaultProvider());
    }

    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     * If the value isn't specified the provided default value will be returned.
     *
     * @param key The context property
     * @param defaultProvider the default value provider
     * @param <K> The type which is identified by the property
     *
     * @return the value of the context property or the default value if it's not specified.
     */
    public <K> K getOrElse(ContextProperty<K> key, DefaultProvider<K> defaultProvider)
    {

        @SuppressWarnings("unchecked")
        K val = (K)properties.get(key);
        if (val == null)
        {
            return defaultProvider.defaultValue();
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
}
