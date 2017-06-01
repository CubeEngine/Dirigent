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
package org.cubeengine.dirigent.parser;

import org.cubeengine.dirigent.formatter.Formatter;

/**
 * An object describing the result of a macro resolution.
 */
public final class MacroResolutionResult
{
    /**
     * Holds a constant for a {@link MacroResolutionResult} representing the state {@link
     * MacroResolutionState#UNKNOWN_NAME} indicating that no formatter exists for the given macro name.
     */
    public static final MacroResolutionResult UNKNOWN_NAME = new MacroResolutionResult(
        MacroResolutionState.UNKNOWN_NAME, null);
    /**
     * Holds a constant for a {@link MacroResolutionResult} representing the state {@link
     * MacroResolutionState#NONE_APPLICABLE} indicating that a formatter exists for the given macro name, but the isn't
     * applicable to the given message input parameter.
     */
    public static final MacroResolutionResult NONE_APPLICABLE = new MacroResolutionResult(
        MacroResolutionState.NONE_APPLICABLE, null);

    private final MacroResolutionState state;
    private final Formatter<?> formatter;

    /**
     * Constructor.
     *
     * @param state The resolution state.
     * @param formatter The formatter.
     */
    public MacroResolutionResult(MacroResolutionState state, Formatter<?> formatter)
    {
        this.state = state;
        this.formatter = formatter;
    }

    /**
     * Returns the information whether the {@link MacroResolutionState} is {@link MacroResolutionState#OK} whcih means
     * that a formatter could be determined for a {@link org.cubeengine.dirigent.parser.element.Macro}.
     *
     * @return whether the state is {@link MacroResolutionState#OK}.
     */
    public boolean isOK()
    {
        return getState() == MacroResolutionState.OK;
    }

    /**
     * Returns the {@link MacroResolutionState} representing the resolution state.
     *
     * @return the state.
     */
    public MacroResolutionState getState()
    {
        return state;
    }

    /**
     * Returns the found {@link Formatter}.
     *
     * @return the formatter.
     */
    public Formatter<?> getFormatter()
    {
        return formatter;
    }
}
