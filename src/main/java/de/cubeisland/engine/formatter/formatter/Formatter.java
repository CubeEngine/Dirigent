package de.cubeisland.engine.formatter.formatter;

import de.cubeisland.engine.formatter.FormatContext;

import java.util.Set;

public interface Formatter<T>
{
    boolean isApplicable(Class<?> objectType);
    String format(T object, FormatContext flags);

    Set<String> names();
}
