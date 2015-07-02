package de.cubeisland.engine.messagecompositor.parser.formatter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
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
