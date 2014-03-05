/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.formatter.formatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.formatter.context.MacroContext;
import de.cubeisland.engine.formatter.formatter.reflected.AnnotationMissingException;
import de.cubeisland.engine.formatter.formatter.reflected.Format;
import de.cubeisland.engine.formatter.formatter.reflected.Names;

public abstract class ReflectedFormatter extends AbstractFormatter<Object>
{
    private Map<Class, Method> formats = new HashMap<Class, Method>();

    protected ReflectedFormatter()
    {
        if (this.getClass().isAnnotationPresent(Names.class))
        {
            this.names.addAll(Arrays.asList(this.getClass().getAnnotation(Names.class).value()));
        }
        else
        {
            throw new AnnotationMissingException(Names.class);
        }
        for (Method method : this.getClass().getMethods())
        {
            if (method.getName().equals("format") && method.isAnnotationPresent(Format.class))
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (method.getReturnType() == String.class &&
                    parameterTypes.length == 2 &&
                    parameterTypes[1] == MacroContext.class)
                {
                    method.setAccessible(true);
                    this.formats.put(parameterTypes[0], method);
                }
                else
                {
                    throw new IllegalArgumentException("The format Methods must have the Object to format and a FormatContext as parameters");
                }
            }
        }
        if (this.formats.isEmpty())
        {
            throw new AnnotationMissingException(Format.class);
        }
    }

    public final String process(Object object, MacroContext context)
    {
        for (Class<?> tClass : formats.keySet())
        {
            if (tClass.isAssignableFrom(object.getClass()))
            {
                try
                {
                    return (String)formats.get(tClass).invoke(this, object, context);
                }
                catch (IllegalAccessException e)
                {
                    throw new IllegalArgumentException(e); // This cannot happen as it got checked before
                }
                catch (InvocationTargetException e)
                {
                    throw new IllegalArgumentException(e); // This cannot happen as it got checked before
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> names()
    {
        return this.names;
    }

    @Override
    public boolean isApplicable(Class<?> objectType)
    {
        for (Class<?> tClass : formats.keySet())
        {
            if (tClass.isAssignableFrom(objectType))
            {
                return true;
            }
        }
        return false;
    }
}
