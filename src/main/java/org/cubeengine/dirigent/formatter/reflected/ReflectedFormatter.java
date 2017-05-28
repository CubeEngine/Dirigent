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
package org.cubeengine.dirigent.formatter.reflected;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;

import static java.util.Arrays.asList;

/**
 * <p>
 *     A Formatter using annotations and reflection to allow multiple Classes to be processes by the same Formatter
 * </p>
 * <p>
 *     An implemented ReflectedFormatter needs a @Names Annotation on its declaration and at least one Method like this:
 * </p>
 * <p>
 *     public String format(T object, MacroContext context) with a @Format Annotation
 * </p>
 */
public abstract class ReflectedFormatter extends Formatter<Object>
{
    private Map<Class<?>, Formatter> formats = new HashMap<Class<?>, Formatter>();
    private Set<String> names;

    protected ReflectedFormatter()
    {
        if (!this.getClass().isAnnotationPresent(Names.class))
        {
            throw new AnnotationMissingException(Names.class);
        }
        names = new HashSet<String>(asList(this.getClass().getAnnotation(Names.class).value()));
        this.findFormatMethods();
        if (this.formats.isEmpty())
        {
            throw new AnnotationMissingException(Format.class);
        }
    }

    private void findFormatMethods()
    {
        for (Method method : this.getClass().getMethods())
        {
            Format formatAnnotation = method.getAnnotation(Format.class);
            if (formatAnnotation != null)
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0)
                {
                    throw new IllegalArgumentException("Format methods must take at least 1 parameter!");
                }
                this.formats.put(parameterTypes[0], createFormatter(method, parameterTypes, formatAnnotation.value()));
            }
        }
    }

    private Formatter createFormatter(Method method, Class<?>[] sig, int prio)
    {
        if (method.getReturnType() != Component.class)
        {
            throw new InvalidFormatMethodException(getClass(), method, "Format methods must return Component!");
        }

        final Invoker invoker;
        if (sig.length == 1)
        {
            invoker = new InputOnly();
        }
        else if (sig.length == 2)
        {
            if (sig[1] == Context.class)
            {
                invoker = new ContextOnly();
            }
            else if (sig[1] == Arguments.class)
            {
                invoker = new ArgsOnly();
            }
            else
            {
                throw new InvalidFormatMethodException(getClass(), method, "Format methods may only take Context and Arguments parameters!");
            }
        }
        else if (sig.length == 3)
        {
            if (sig[1] == Context.class && sig[2] == Arguments.class)
            {
                invoker = new CompleteContextFirst();
            }
            else if (sig[1] == Arguments.class && sig[2] == Context.class)
            {
                invoker = new CompleteArgsFirst();
            }
            else
            {
                throw new InvalidFormatMethodException(getClass(), method, "Format methods may only take Context and Arguments parameters!");
            }
        }
        else
        {
            throw new InvalidFormatMethodException(getClass(), method, "Format methods must take at most 3 parameters!");
        }
        return new Formatter(this, method, prio, invoker);
    }

    @Override
    protected Component format(final Object input, Context context, Arguments args)
    {
        final Class<?> inputClass = input.getClass();
        final List<Formatter> candidates = new ArrayList<Formatter>();
        for (Entry<Class<?>, Formatter> entry : formats.entrySet())
        {
            Class<?> formatterClass = entry.getKey();
            if (inputClass == formatterClass)
            {
                // exact match will be used directly
                return this.formats.get(formatterClass).format(input, context, args);
            }
            else if (formatterClass.isAssignableFrom(inputClass))
            {
                candidates.add(entry.getValue());
            }
        }
        if (candidates.isEmpty())
        {
            return null;
        }
        Collections.sort(candidates, new Comparator<ReflectedFormatter.Formatter>()
        {
            @Override
            public int compare(ReflectedFormatter.Formatter a, ReflectedFormatter.Formatter b)
            {
                // sort descending, so the highest priority value will be first.
                return b.prio - a.prio;
            }
        });
        return candidates.get(0).format(input, context, args);
    }

    @Override
    public Set<String> names()
    {
        return this.names;
    }

    @Override
    public boolean isApplicable(Object input)
    {
        if (input == null)
        {
            return false;
        }
        for (Class<?> tClass : formats.keySet())
        {
            if (tClass.isAssignableFrom(input.getClass()))
            {
                return true;
            }
        }
        return false;
    }

    private static final class Formatter
    {
        final Object host;
        final Method method;
        final int prio;
        final Invoker invoker;

        public Formatter(Object host, Method method, int prio, Invoker invoker)
        {
            this.host = host;
            this.method = method;
            this.prio = prio;
            this.invoker = invoker;
        }

        public final Component format(Object in, Context ctx, Arguments args)
        {
            try
            {
                return (Component)invoker.invoke(host, method, in, ctx, args);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private interface Invoker
    {
        Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException;
    }

    private static final class CompleteContextFirst implements Invoker
    {
        @Override
        public Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, ctx, args);
        }
    }

    private static final class CompleteArgsFirst implements Invoker
    {
        @Override
        public Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, args, ctx);
        }
    }

    private static final class ContextOnly implements Invoker
    {
        @Override
        public Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, ctx);
        }
    }

    private static final class ArgsOnly implements Invoker
    {
        @Override
        public Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, args);
        }
    }

    private static final class InputOnly implements Invoker
    {
        @Override
        public Object invoke(Object host, Method method, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in);
        }
    }
}
