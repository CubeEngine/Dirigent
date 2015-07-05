package de.cubeisland.engine.messagecompositor.parser;

public class StringCompositor extends BuilderMessageCompositor<String, StringBuilder>
{
    public StringCompositor()
    {
        super(new StringMessageBuilder());
    }
}
