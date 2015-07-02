package de.cubeisland.engine.messagecompositor.parser.formatter;

import java.util.List;
import java.util.Locale;
import de.cubeisland.engine.messagecompositor.parser.component.argument.Argument;
import de.cubeisland.engine.messagecompositor.parser.component.argument.Flag;
import de.cubeisland.engine.messagecompositor.parser.component.argument.Parameter;

public class Context
{
    private Locale locale;
    private List<Argument> argumentList;

    public Context(Locale locale, List<Argument> argumentList)
    {
        this.argumentList = argumentList;
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public List<Argument> getArgumentList()
    {
        return argumentList;
    }

    public String get(String name)
    {
        for (Argument argument : argumentList)
        {
            if (argument instanceof Parameter)
            {
                if (((Parameter)argument).getName().equals(name))
                {
                    return ((Parameter)argument).getValue();
                }
            }
        }
        return null;
    }

    public String getFlag(int i)
    {
        if (argumentList.size() >= i + 1)
        {
            Argument argument = argumentList.get(i);
            if (argument instanceof Flag)
            {
                return ((Flag)argument).getValue();
            }
        }
        return null;
    }

    public boolean has(String flag)
    {
        for (Argument argument : argumentList)
        {
            if (argument instanceof Flag && ((Flag)argument).getValue().equals(flag))
            {
                return true;
            }
        }
        return false;
    }
}
