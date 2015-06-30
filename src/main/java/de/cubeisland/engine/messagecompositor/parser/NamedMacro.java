package de.cubeisland.engine.messagecompositor.parser;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class NamedMacro implements Macro
{
    private final String name;
    private final List<Argument> args;

    public NamedMacro(String name, List<Argument> args)
    {
        this.name = name;
        this.args = args == null ? Collections.<Argument>emptyList() : unmodifiableList(args);
    }

    public String getName()
    {
        return name;
    }

    public List<Argument> getArgs()
    {
        return args;
    }
}
