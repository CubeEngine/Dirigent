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
package org.cubeengine.dirigent.parser.component;

import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.argument.Arguments;

/**
 * A Component containing the formatter found for a token
 */
public class ResolvedMacro implements Component
{
    /**
     * The formatter which is responsible of formatting the messages input parameter.
     */
    private final Formatter<Object> formatter;
    /**
     * The messages input parameter.
     */
    private final Object input;
    /**
     * The macro arguments.
     */
    private final Arguments arguments;

    /**
     * Constructor.
     *
     * @param formatter The formatter which is responsible of formatting the messages input parameter.
     * @param input The messages input parameter.
     * @param arguments The compose context.
     */
    public ResolvedMacro(Formatter<Object> formatter, Object input, Arguments arguments)
    {
        this.formatter = formatter;
        this.input = input;
        this.arguments = arguments;
    }

    /**
     * Returns the {@link Formatter} which is responsible of formatting the messages input parameters.
     *
     * @return the formatter.
     */
    public Formatter<Object> getFormatter()
    {
        return formatter;
    }

    /**
     * Returns the messages input parameter.
     *
     * @return the input parameter.
     */
    public Object getInput()
    {
        return input;
    }

    /**
     * Returns the macro argument.
     *
     * @return the macro argument.
     */
    public Arguments getArguments()
    {
        return arguments;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ResolvedMacro))
        {
            return false;
        }

        final ResolvedMacro that = (ResolvedMacro)o;

        if (!getFormatter().equals(that.getFormatter()))
        {
            return false;
        }
        if (!getInput().equals(that.getInput()))
        {
            return false;
        }
        return getArguments().equals(that.getArguments());
    }

    @Override
    public int hashCode()
    {
        int result = getFormatter().hashCode();
        result = 31 * result + getInput().hashCode();
        result = 31 * result + getArguments().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ResolvedMacro{" + "formatter=" + formatter + ", input=" + input + ", arguments=" + arguments + '}';
    }
}
