package de.cubeisland.engine.messagecompositor.parser;

import java.util.LinkedList;

public class Message
{
    private LinkedList<Element> elements = new LinkedList<Element>();

    public Message(LinkedList<Element> elements)
    {
        this.elements = elements;
    }
}
