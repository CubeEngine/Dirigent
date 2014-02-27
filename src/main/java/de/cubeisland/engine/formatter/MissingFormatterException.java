package de.cubeisland.engine.formatter;

public class MissingFormatterException extends RuntimeException
{
    public MissingFormatterException(String type, Class clazz)
    {
        super("There is no registered formatter for '" + type + "' supporting " + clazz.getName());
    }
}
