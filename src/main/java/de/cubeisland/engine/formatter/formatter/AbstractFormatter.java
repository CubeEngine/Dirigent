package de.cubeisland.engine.formatter.formatter;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFormatter<T> implements Formatter<T>
{
    protected final Class<T> tClass;
    protected Set<String> names = new HashSet<String>();

    protected AbstractFormatter()
    {
        this.tClass = (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected AbstractFormatter(String... names)
    {
        this();
        this.names.addAll(Arrays.asList(names));
    }

    @Override
    public boolean isApplicable(Class<?> objectType)
    {
        return tClass.isAssignableFrom(objectType);
    }

    @Override
    public Set<String> names()
    {
        return this.names;
    }
}
