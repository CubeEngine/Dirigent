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
import org.cubeengine.dirigent.parser.MacroResolutionState;
import org.cubeengine.dirigent.parser.token.Macro;

/**
 * A Component signaling a no Formatter could be found for a token
 */
public class UnresolvableMacro implements Component
{
    private Macro macro;
    private Object arg;
    private final MacroResolutionState state;

    public UnresolvableMacro(Macro macro, Object arg, MacroResolutionState state)
    {
        this.macro = macro;
        this.arg = arg;
        this.state = state;
    }

    public Macro getMacro()
    {
        return macro;
    }

    public Object getInput()
    {
        return arg;
    }

    public MacroResolutionState getState()
    {
        return state;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof UnresolvableMacro))
        {
            return false;
        }

        final UnresolvableMacro that = (UnresolvableMacro)o;

        if (!getMacro().equals(that.getMacro()))
        {
            return false;
        }
        return getInput().equals(that.getInput());
    }

    @Override
    public int hashCode()
    {
        int result = getMacro().hashCode();
        result = 31 * result + getInput().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "UnresolvableMacro{" + "macro=" + macro + ", arg=" + arg + '}';
    }
}
