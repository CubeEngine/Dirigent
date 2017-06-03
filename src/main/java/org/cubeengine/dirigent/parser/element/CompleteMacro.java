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
package org.cubeengine.dirigent.parser.element;

import org.cubeengine.dirigent.context.Arguments;

/**
 * A complete macro with name, position and optional arguments.
 */
public class CompleteMacro extends NamedMacro implements Indexed
{
    /**
     * The position index of the message input parameter which shall be formatted with this macro.
     */
    private int index;

    /**
     * Constructor.
     *
     * @param index The position index of the message input parameter which shall be formatted with this macro.
     * @param name The name of the macros.
     * @param args The arguments of the macro.
     */
    public CompleteMacro(int index, String name, Arguments args)
    {
        super(name, args);
        this.index = index;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof CompleteMacro))
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        final CompleteMacro that = (CompleteMacro)o;

        return getIndex() == that.getIndex();
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + getIndex();
        return result;
    }

    @Override
    public String toString()
    {
        return "CompleteMacro{" + "index=" + index + ", name='" + getName() + '\'' + ", args=" + getArgs() + "}";
    }
}
