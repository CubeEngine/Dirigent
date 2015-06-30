package de.cubeisland.engine.messagecompositor.parser;

import java.util.List;

public class CompleteMacro extends NamedMacro implements Indexed
{
    private int index;

    public CompleteMacro(int index, String name, List<Argument> args)
    {
        super(name, args);
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }
}
