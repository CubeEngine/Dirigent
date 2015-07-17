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

import java.util.List;
import java.util.Locale;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Flag;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

public class Context
{
    private Locale locale;
    private List<Argument> argumentList;

    public Context(Locale locale)
    {
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public List<Argument> getArgumentList()
    {
        return argumentList;
    }

    public Context with(List<Argument> list)
    {
        this.argumentList = list;
        return this;
    }

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

    public String getFlag(int i)
    {
        if (argumentList.size() >= i + 1)
        {
            Argument argument = argumentList.get(i);
            if (argument instanceof Flag)
            {
                return ((Flag)argument).getValue();
            }
        }
        return null;
    }

    public boolean has(String flag)
    {
        for (Argument argument : argumentList)
        {
            if (argument instanceof Flag && ((Flag)argument).getValue().equals(flag))
            {
                return true;
            }
        }
        return false;
    }
}
