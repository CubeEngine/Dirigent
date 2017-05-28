package org.cubeengine.dirigent.builder;

import java.util.Set;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;

/**
 * A formatter reversing string result to use as a default formatter for the tests.
 */
public class ReverseStringFormatter extends Formatter<Object>
{
    @Override
    public boolean isApplicable(Object input)
    {
        return true;
    }

    @Override
    protected Component format(Object input, Context context, Arguments args)
    {
        return new Text(new StringBuilder(String.valueOf(input)).reverse().toString());
    }

    @Override
    public Set<String> names()
    {
        return null;
    }
}
