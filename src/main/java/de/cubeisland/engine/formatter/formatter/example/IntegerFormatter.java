package de.cubeisland.engine.formatter.formatter.example;

import java.util.Arrays;
import java.util.HashSet;

import de.cubeisland.engine.formatter.context.FormatContext;
import de.cubeisland.engine.formatter.formatter.AbstractFormatter;

public class IntegerFormatter extends AbstractFormatter<Integer>
{
    public IntegerFormatter()
    {
        super(new HashSet<String>(Arrays.asList("number")));
    }

    public String format(Integer object, FormatContext context)
    {
        return String.valueOf(object);
    }
}
