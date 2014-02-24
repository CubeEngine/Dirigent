package de.cubeisland.engine.formatter.formatter;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractFormatter<T> implements Formatter<T>
{
    private final Class<T> tClass;

    protected AbstractFormatter()
    {
        this.tClass = (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public boolean isApplicable(Class objectType)
    {
        return tClass.isAssignableFrom(objectType);
    }
}
