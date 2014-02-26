package de.cubeisland.engine.formatter.formatter.reflected;

import de.cubeisland.engine.formatter.FormatContext;
import de.cubeisland.engine.formatter.formatter.AbstractFormatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        for (Method method : this.getClass().getDeclaredMethods())
        {
            if (method.getName().equals("format") && method.isAnnotationPresent(Format.class))
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (method.getReturnType() == String.class &&
                        parameterTypes.length == 2 &&
                        parameterTypes[0] == method.getAnnotation(Format.class).value() &&
                        parameterTypes[1] == FormatContext.class) {
                    method.setAccessible(true);
                    this.formats.put(method.getAnnotation(Format.class).value(), method);
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

    @Override
    public final String format(Object object, FormatContext flags)
    {
        for (Class<?> tClass : formats.keySet())
        {
            if (tClass.isAssignableFrom(object.getClass()))
            {
                try
                {
                    return (String) formats.get(tClass).invoke(this, object, flags);
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
