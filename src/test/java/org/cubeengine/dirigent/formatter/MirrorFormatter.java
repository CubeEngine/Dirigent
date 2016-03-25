package org.cubeengine.dirigent.formatter;

import java.util.Collections;
import java.util.Set;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Flag;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

/**
 * A formatter which is used to test {@link ConstantFormatter}s.
 * It mirrors a complete macro, just the labels are gone.
 */
public class MirrorFormatter extends ConstantFormatter
{
    private final String mirrorName;

    public MirrorFormatter(final String name)
    {
        this.mirrorName = name.trim();
    }

    @Override
    public Component format(final Context context)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(this.mirrorName);
        for (final Argument argument : context.getArgumentList())
        {
            builder.append(':');
            if (argument instanceof Flag)
            {
                builder.append(((Flag)argument).getValue());
            }
            else if (argument instanceof Parameter)
            {
                builder.append(((Parameter)argument).getName());
                builder.append("=");
                builder.append(((Parameter)argument).getValue());
            }
        }
        builder.append("}");

        return new Text(builder.toString());
    }

    @Override
    public Set<String> names()
    {
        return Collections.singleton(this.mirrorName);
    }
}
