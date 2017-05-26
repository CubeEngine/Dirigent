package org.cubeengine.dirigent.formatter;

import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;

/**
 * The string formatter formats an {@link Object} with {@link String#valueOf(Object)}. It is possible to control this
 * output with one of the flags "lowercase" or "uppercase" which lowercase or uppercase the string.
 */
public class StringFormatter extends AbstractFormatter<Object>
{
    /**
     * The label of the lowercase flag.
     */
    static final String LOWERCASE_FLAG = "lowercase";
    /**
     * The label of the uppercase flag.
     */
    static final String UPPERCASE_FLAG = "uppercase";

    /**
     * Constructor. Initializes this formatter with a few default names.
     */
    public StringFormatter()
    {
        this("string");
    }

    /**
     * Constructor.
     *
     * @param names The names triggering this formatter.
     */
    public StringFormatter(String... names)
    {
        super(names);
    }

    @Override
    protected Component format(Object arg, Context context)
    {
        return new Text(parseObjectToString(arg, context));
    }

    /**
     * Parses the given object to a string depending on the context.
     *
     * @param object  The object to parse.
     * @param context The context to use.
     *
     * @return The object as a string.
     */
    protected String parseObjectToString(Object object, Context context)
    {
        final String string = String.valueOf(object);
        if (context.has(LOWERCASE_FLAG))
        {
            return string.toLowerCase(context.getLocale());
        }
        if (context.has(UPPERCASE_FLAG))
        {
            return string.toUpperCase(context.getLocale());
        }
        return string;
    }
}
