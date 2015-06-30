package de.cubeisland.engine.messagecompositor.parser;

public class IndexedDefaultMacro extends DefaultMacro implements Indexed
{
    private final int index;

    public IndexedDefaultMacro(int index)
    {
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }
}
