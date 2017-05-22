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
package org.cubeengine.dirigent.parser.component.macro;

import org.cubeengine.dirigent.parser.component.ErrorComponent;
import org.cubeengine.dirigent.parser.component.Text;

/**
 * Show the original text of an invalid macro.
 * Gets added to the Message when an invalid macro is encountered.
 */
public class IllegalMacro extends Text implements ErrorComponent
{
    private String error;

    public IllegalMacro(String string, String error)
    {
        super(string);
        this.error = error;
    }

    @Override
    public String getError()
    {
        return error;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof IllegalMacro))
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        final IllegalMacro that = (IllegalMacro)o;

        return getError().equals(that.getError());
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + getError().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "IllegalMacro{" + "error='" + error + '\'' + ", string='" + getString() + '\'' + '}';
    }
}
