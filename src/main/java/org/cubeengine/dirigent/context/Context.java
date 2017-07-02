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

import java.util.Locale;

/**
 * This interface specifies a context of a compose process which is triggered by the
 * {@link org.cubeengine.dirigent.Dirigent#compose(Context, String, Object...)} method. This might include a
 * {@link Locale} or other information which can be used by the formatters to buildText proper messages.
 */
public interface Context
{
    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     *
     * @param key the context property
     * @param <K> the type which is identified by the property
     *
     * @return the value of the context property.
     */
    <K> K get(ContextProperty<K> key);

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
    <K> K getOrElse(ContextProperty<K> key, DefaultProvider<K> defaultProvider);

    /**
     * Sets a new property and returns a new immutable context.
     *
     * @param key the key
     * @param value the value
     * @param <K> the key type
     * @return the new context instance
     */
    <K> Context set(ContextProperty<K> key, K value);

    /**
     * Sets new properties and returns a new immutable context.
     *
     * @param mappings the new mappings
     * @return the new context instance
     */
    Context set(PropertyMapping<?>... mappings);
}
