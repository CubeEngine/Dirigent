package de.cubeisland.engine.messagecompositor.parser;

public class Text implements MessageComponent
{
    private String string;

    public Text(String string)
    {
        this.string = string;
    }
}
