package org.cubeengine.dirigent.formatter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;

/**
 * This is a constant formatter handling static text. It could be used to indicate text parts of a message which must
 * not be localized by the localizer. Therefore it simply writes the text of the first argument to the message.
 */
public class StaticTextFormatter extends ConstantFormatter
{
    /**
     * The names of this formatter.
     */
    private final Set<String> names;

    /**
     * Constructor. Initializes this formatter with a few default names.
     */
    public StaticTextFormatter()
    {
        this("text");
    }

    /**
     * Constructor.
     *
     * @param names The names triggering this formatter.
     */
    public StaticTextFormatter(String... names)
    {
        if (names == null || names.length == 0)
        {
            throw new IllegalArgumentException("This macro needs a name.");
        }

        this.names = new HashSet<String>(names.length);
        Collections.addAll(this.names, names);
    }

    @Override
    public Component format(Context context)
    {
        return new Text(context.get(0));
    }

    @Override
    public Set<String> names()
    {
        return names;
    }
}
