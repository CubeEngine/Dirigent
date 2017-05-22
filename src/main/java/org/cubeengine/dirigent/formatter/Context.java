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

import java.util.List;
import java.util.Locale;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Value;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

/**
 * The Context of a Macro including Locale and the arguments of the Macro if any
 */
public class Context
{
    private Locale locale;
    private List<Argument> argumentList;

    public Context(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the Locale
     * @return the locale
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Returns the list of arguments
     * @return the list of arguments
     */
    public List<Argument> getArgumentList()
    {
        return argumentList;
    }

    /**
     * Replaces the list of arguments allowing to reuse the context
     * @param list the list to replace with
     * @return fluent interface
     */
    public Context with(List<Argument> list)
    {
        this.argumentList = list;
        return this;
    }

    /**
     * Returns the Arguments value for given name or null if not found
     * @param name the name
     * @return the value of the Argument by name
     */
    public String get(String name)
    {
        for (Argument argument : argumentList)
        {
            if (argument instanceof Parameter)
            {
                if (((Parameter)argument).getName().equals(name))
                {
                    return ((Parameter)argument).getValue();
                }
            }
        }
        return null;
    }

    /**
     * Gets the Value of an Argument at a position.
     *
     * @param i the position
     * @return the Argument value
     */
    public String get(int i)
    {
        if (argumentList.size() >= i + 1)
        {
            Argument argument = argumentList.get(i);
            if (argument instanceof Value)
            {
                return ((Value)argument).getValue();
            }
        }
        return null;
    }

    /**
     * Returns whether the list of arguments contains given flag
     * @param value the flag to check for
     * @return whether the list of arguments contains given flag
     */
    public boolean has(String value)
    {
        for (Argument argument : argumentList)
        {
            if (argument instanceof Value && ((Value)argument).getValue().equals(value))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Context))
        {
            return false;
        }

        final Context context = (Context)o;

        if (!getLocale().equals(context.getLocale()))
        {
            return false;
        }
        return getArgumentList().equals(context.getArgumentList());
    }

    @Override
    public int hashCode()
    {
        int result = getLocale().hashCode();
        result = 31 * result + getArgumentList().hashCode();
        return result;
    }
}
