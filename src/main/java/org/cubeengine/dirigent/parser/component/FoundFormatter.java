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
package org.cubeengine.dirigent.parser.component;

import java.util.List;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.formatter.Context;
import org.cubeengine.dirigent.formatter.Formatter;

/**
 * A Component containing the formatter found for a macro
 */
public class FoundFormatter implements Component
{
    private final Formatter found;
    private final Object arg;
    private final List<Argument> arguments;
    private Context context;

    public FoundFormatter(Formatter found, Object arg, List<Argument> arguments, Context context)
    {

        this.found = found;
        this.arg = arg;
        this.arguments = arguments;
        this.context = context;
    }

    public Formatter getFound()
    {
        return found;
    }

    public Object getArg()
    {
        return arg;
    }

    public Context getContext()
    {
        return context.with(arguments);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof FoundFormatter))
        {
            return false;
        }

        final FoundFormatter that = (FoundFormatter)o;

        if (!getFound().equals(that.getFound()))
        {
            return false;
        }
        if (!getArg().equals(that.getArg()))
        {
            return false;
        }
        if (!arguments.equals(that.arguments))
        {
            return false;
        }
        return getContext().equals(that.getContext());
    }

    @Override
    public int hashCode()
    {
        int result = getFound().hashCode();
        result = 31 * result + getArg().hashCode();
        result = 31 * result + arguments.hashCode();
        result = 31 * result + getContext().hashCode();
        return result;
    }
}
