package de.cubeisland.engine.messagecompositor.parser;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Message
{
    private final List<MessageComponent> components;

    public Message(List<MessageComponent> components)
    {
        this.components = unmodifiableList(components);
    }

    public List<MessageComponent> getComponents()
    {
        return components;
    }
}
