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

import org.cubeengine.dirigent.parser.MacroResolutionState;
import org.cubeengine.dirigent.parser.element.Macro;

/**
 * A Component signaling that a Formatter could not be resolved. This might be because a formatter couldn't be found for
 * a macro.
 */
public class UnresolvableMacro implements Component
{
    /**
     * The macro which couldn't be resolved.
     */
    private Macro macro;
    /**
     * The messages input parameter.
     */
    private Object input;
    /**
     * The macro resolution state.
     */
    private final MacroResolutionState state;

    /**
     * Constructor.
     *
     * @param macro The macro which couldn't be resolved.
     * @param input The messages input parameter.
     * @param state The macro resolution state.
     */
    public UnresolvableMacro(Macro macro, Object input, MacroResolutionState state)
    {
        this.macro = macro;
        this.input = input;
        this.state = state;
    }

    /**
     * Returns the macro which couldn't be resolved.
     *
     * @return the macro.
     */
    public Macro getMacro()
    {
        return macro;
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
     * Returns the macro resolution state indicating the reason for being unresolved.
     *
     * @return the macro resolution state.
     */
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
        return "UnresolvableMacro{" + "macro=" + macro + ", input=" + input + '}';
    }
}
