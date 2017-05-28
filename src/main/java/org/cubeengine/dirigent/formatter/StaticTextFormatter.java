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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;

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
     * Constructs this formatter with a few default names.
     */
    public StaticTextFormatter()
    {
        this("text");
    }

    /**
     * Constructs this formatter with the given names.
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
    public Component format(Context context, Arguments args)
    {
        return new Text(args.get(0));
    }

    @Override
    public Set<String> names()
    {
        return names;
    }
}
