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

public class ContextProperty<K>
{
    private final DefaultProvider<K> defaultProvider;

    public ContextProperty()
    {
        this(new DefaultProvider<K>() {
            @Override
            public K defaultValue(Context context)
            {
                return null;
            }
        });
    }

    public ContextProperty(DefaultProvider<K> defaultProvider)
    {
        this.defaultProvider = defaultProvider;
    }

    public final PropertyMapping<K> with(K value)
    {
        return new PropertyMapping<K>(this, value);
    }

    /**
     * Provides the default value for the given key, if the key has no value assigned.
     *
     * @return the default value, might be null.
     */
    public DefaultProvider<K> getDefaultProvider() {
        return defaultProvider;
    }
}
