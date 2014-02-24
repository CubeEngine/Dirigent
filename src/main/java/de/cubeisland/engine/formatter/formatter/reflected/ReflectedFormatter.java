package de.cubeisland.engine.formatter.formatter.reflected;

import de.cubeisland.engine.formatter.formatter.AbstractFormatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ReflectedFormatter<T> extends AbstractFormatter<T>
{
    private final Set<String> names;

    protected ReflectedFormatter()
    {
        if (this.getClass().isAnnotationPresent(Names.class))
        {
            this.names = new HashSet<String>(Arrays.asList(this.getClass().getAnnotation(Names.class).value()));
        }
        else
        {
            throw new AnnotationMissingException(Names.class);
        }
    }

    @Override
    public Set<String> names()
    {
        return this.names;
    }
}
