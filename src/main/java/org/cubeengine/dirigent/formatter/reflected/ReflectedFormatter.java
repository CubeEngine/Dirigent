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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private Map<Class, FormatterInvoker> formats = new HashMap<Class, FormatterInvoker>();
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
            if (method.isAnnotationPresent(Format.class))
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0)
                {
                    throw new IllegalArgumentException("Format methods must take at least 1 parameter!");
                }
                this.formats.put(parameterTypes[0], createInvoker(method, parameterTypes));
            }
        }
    }

    private FormatterInvoker createInvoker(Method method, Class<?>[] sig)
    {
        if (method.getReturnType() != Component.class)
        {
            throw new InvalidFormatMethodException(getClass(), method, "Format methods must return Component!");
        }

        if (sig.length == 1)
        {
            return new InputOnly(method);
        }
        else if (sig.length == 2)
        {
            if (sig[1] == Context.class)
            {
                return new ContextOnly(method);
            }
            else if (sig[1] == Arguments.class)
            {
                return new ArgsOnly(method);
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
                return new CompleteContextFirst(method);
            }
            else if (sig[1] == Arguments.class && sig[2] == Context.class)
            {
                return new CompleteArgsFirst(method);
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
    }

    @Override
    protected Component format(final Object input, Context context, Arguments args)
    {
        final Class<?> inputClass = input.getClass();
        Class<?> candidate = null;
        for (Class<?> formatterClass : formats.keySet())
        {
            if (inputClass == formatterClass)
            {
                return this.formats.get(formatterClass).format(input, context, args);
            }
            else if (formatterClass.isAssignableFrom(inputClass))
            {
                candidate = formatterClass;
            }
        }
        if (candidate == null)
        {
            return null;
        }
        return formats.get(candidate).format(input, context, args);
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

    private abstract class FormatterInvoker
    {
        final Method method;

        FormatterInvoker(Method method)
        {
            this.method = method;
        }

        public final Component format(Object in, Context ctx, Arguments args)
        {
            try
            {
                return (Component)invoke(ReflectedFormatter.this, in, ctx, args);
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

        public abstract Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException;
    }

    private final class CompleteContextFirst extends FormatterInvoker
    {
        CompleteContextFirst(Method method)
        {
            super(method);
        }

        @Override
        public Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, ctx, args);
        }
    }

    private final class CompleteArgsFirst extends FormatterInvoker
    {
        CompleteArgsFirst(Method method)
        {
            super(method);
        }

        @Override
        public Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, args, ctx);
        }
    }

    private final class ContextOnly extends FormatterInvoker
    {
        ContextOnly(Method method)
        {
            super(method);
        }

        @Override
        public Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, ctx);
        }
    }

    private final class ArgsOnly extends FormatterInvoker
    {
        ArgsOnly(Method method)
        {
            super(method);
        }

        @Override
        public Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in, args);
        }
    }

    private final class InputOnly extends FormatterInvoker
    {
        InputOnly(Method method)
        {
            super(method);
        }

        @Override
        public Object invoke(Object host, Object in, Context ctx, Arguments args) throws InvocationTargetException, IllegalAccessException
        {
            return method.invoke(host, in);
        }
    }
}
