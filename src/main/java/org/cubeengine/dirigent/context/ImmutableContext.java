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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * This {@link Context} implementation cannot be changed after creation.
 */
public class ImmutableContext implements Context
{
    private final Map<ContextProperty<?>, Object> properties;

    private ImmutableContext(Map<ContextProperty<?>, Object> properties)
    {
        this.properties = properties;
    }

    public <K> K get(ContextProperty<K> key)
    {
        return getOrElse(key, key.getDefaultProvider());
    }

    public <K> Context set(ContextProperty<K> key, K value)
    {
        Map<ContextProperty<?>, Object> values = cloneProperties(this.properties);
        values.put(key, value);
        return new ImmutableContext(values);
    }

    public Context set(PropertyMapping<?>... mappings)
    {
        Map<ContextProperty<?>, Object> values = cloneProperties(this.properties);
        for (final PropertyMapping<?> mapping : mappings)
        {
            values.put(mapping.property, mapping.value);
        }
        return new ImmutableContext(values);
    }

    public <K> K getOrElse(ContextProperty<K> key, DefaultProvider<K> defaultProvider)
    {

        @SuppressWarnings("unchecked")
        K val = (K)properties.get(key);
        if (val == null)
        {
            return defaultProvider.defaultValue(this);
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

        final ImmutableContext context = (ImmutableContext)o;

        return properties.equals(context.properties);
    }

    @Override
    public int hashCode()
    {
        return properties.hashCode();
    }

    private static Map<ContextProperty<?>, Object> cloneProperties(Map<ContextProperty<?>, Object> properties)
    {
        return new IdentityHashMap<ContextProperty<?>, Object>(properties);
    }

    public static ImmutableContext create(Map<ContextProperty<?>, Object> properties)
    {
        return new ImmutableContext(cloneProperties(properties));
    }

    static ImmutableContext createUnsafe(IdentityHashMap<ContextProperty<?>, Object> properties)
    {
        return new ImmutableContext(properties);
    }
}
