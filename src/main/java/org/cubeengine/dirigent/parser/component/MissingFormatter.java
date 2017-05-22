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

import org.cubeengine.dirigent.parser.component.macro.Macro;

/**
 * A Component signaling a no Formatter could be found for a macro
 */
public class MissingFormatter implements ErrorComponent
{
    private Macro missing;
    private Object arg;

    public MissingFormatter(Macro missing, Object arg)
    {
        this.missing = missing;
        this.arg = arg;
    }

    public Macro getMissing()
    {
        return missing;
    }

    public Object getArg()
    {
        return arg;
    }

    @Override
    public String getError()
    {
        return "Formatter not found";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof MissingFormatter))
        {
            return false;
        }

        final MissingFormatter that = (MissingFormatter)o;

        if (!getMissing().equals(that.getMissing()))
        {
            return false;
        }
        return getArg().equals(that.getArg());
    }

    @Override
    public int hashCode()
    {
        int result = getMissing().hashCode();
        result = 31 * result + getArg().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "MissingFormatter{" + "missing=" + missing + ", arg=" + arg + '}';
    }
}
