package de.cubeisland.engine.formatter.formatter.reflected;

import java.lang.annotation.Annotation;

public class AnnotationMissingException extends RuntimeException
{
    public AnnotationMissingException(Class<? extends Annotation> clazz)
    {
        super("This formatter is missing a @" + clazz.getName() + " Annotation!");
    }
}
