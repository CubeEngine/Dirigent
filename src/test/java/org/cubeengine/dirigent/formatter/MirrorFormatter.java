/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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
import java.util.Set;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Value;
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
            if (argument instanceof Value)
            {
                builder.append(((Value)argument).getValue());
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
