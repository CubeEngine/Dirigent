/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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
package de.cubeisland.engine.dirigent.parser.formatter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public abstract class AbstractFormatter<T> extends Formatter<T>
{
    private final Class<T> clazz;
    private Set<String> names;

    public AbstractFormatter(Class<T> clazz, String... names)
    {
        this(clazz, new HashSet<String>(asList(names)));
    }

    public AbstractFormatter(Class<T> clazz, Set<String> names)
    {
        this.clazz = clazz;
        this.names = unmodifiableSet(names);
    }

    public AbstractFormatter(String... names)
    {
        this(new HashSet<String>(asList(names)));
    }

    public AbstractFormatter(Set<String> names)
    {
        this.clazz = getClazz(getClass());
        this.names = names;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClazz(Class<?> thisClass)
    {
        Type genericSuperclass = thisClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType)
        {
            return  (Class<T>)((ParameterizedType)genericSuperclass).getActualTypeArguments()[0];
        }
        else
        {
            return  (Class<T>)Object.class;
        }
    }

    @Override
    public boolean isApplicable(Object arg)
    {
        return clazz.isAssignableFrom(arg.getClass());
    }

    @Override
    public Set<String> names()
    {
        return this.names;
    }
}
