package de.cubeisland.engine.messagecompositor.parser;

import java.util.List;

public class MacroElement implements Element
{
    private final Integer index;
    private final String name;
    private final List<ArgumentElement> arguments;

    public MacroElement(Integer index, String name, List<ArgumentElement> arguments)
    {
        this.index = index;
        this.name = name;
        this.arguments = arguments;
    }
}
