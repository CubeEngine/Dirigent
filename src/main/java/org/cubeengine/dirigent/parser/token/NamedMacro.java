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
package org.cubeengine.dirigent.parser.token;

import java.util.List;

import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.formatter.argument.Argument;

/**
 * A macro with a name and an optional list of arguments.
 */
public class NamedMacro implements Macro
{
    /**
     * The name of the macro.
     */
    private final String name;
    /**
     * The arguments of the macro.
     */
    private final Arguments args;

    /**
     * Constructor.
     *
     * @param name The name of the macros.
     * @param args The arguments of the macro.
     */
    public NamedMacro(String name, List<Argument> args)
    {
        this.name = name;
        this.args = Arguments.create(args);
    }

    /**
     * Returns the name of the macro.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the arguments of the macro.
     *
     * @return the arguments.
     */
    public Arguments getArgs()
    {
        return args;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof NamedMacro))
        {
            return false;
        }

        final NamedMacro that = (NamedMacro)o;

        if (!getName().equals(that.getName()))
        {
            return false;
        }
        return getArgs().equals(that.getArgs());
    }

    @Override
    public int hashCode()
    {
        int result = getName().hashCode();
        result = 31 * result + getArgs().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "NamedMacro{" + "name='" + name + '\'' + ", args=" + args + '}';
    }
}
