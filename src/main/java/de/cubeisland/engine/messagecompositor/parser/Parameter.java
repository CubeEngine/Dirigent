package de.cubeisland.engine.messagecompositor.parser;

public class Parameter implements Argument
{
    private final String name;
    private final String value;

    public Parameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
}
