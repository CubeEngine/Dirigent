/*
 * The MIT License
 * Copyright © 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.dirigent.formatter;

import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.component.Text;

import static org.cubeengine.dirigent.formatter.Context.LOCALE;

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
    protected Component format(Object arg, Context context, Arguments args)
    {
        return new Text(parseObjectToString(arg, context.get(LOCALE), args));
    }

    /**
     * Parses the given object to a string depending on the context.
     *
     * @param object  The object to parse.
     * @param locale The locale to use.
     *
     * @return The object as a string.
     */
    protected String parseObjectToString(Object object, Locale locale, Arguments args)
    {
        final String string = String.valueOf(object);
        if (args.has(LOWERCASE_FLAG))
        {
            return string.toLowerCase(locale);
        }
        if (args.has(UPPERCASE_FLAG))
        {
            return string.toUpperCase(locale);
        }
        return string;
    }
}
