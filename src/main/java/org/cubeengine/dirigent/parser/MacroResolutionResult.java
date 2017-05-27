package org.cubeengine.dirigent.parser;

import org.cubeengine.dirigent.formatter.Formatter;

/**
 * An object describing the result of a macro resolution.
 */
public final class MacroResolutionResult
{
    public static final MacroResolutionResult UNKNOWN_NAME = new MacroResolutionResult(MacroResolutionState.UNKNOWN_NAME, null);
    public static final MacroResolutionResult NONE_APPLICABLE = new MacroResolutionResult(MacroResolutionState.NONE_APPLICABLE, null);

    private final MacroResolutionState state;
    private final Formatter<?> formatter;

    public MacroResolutionResult(MacroResolutionState state, Formatter<?> formatter)
    {
        this.state = state;
        this.formatter = formatter;
    }

    public boolean isOK()
    {
        return getState() == MacroResolutionState.OK;
    }

    public MacroResolutionState getState()
    {
        return state;
    }

    public Formatter<?> getFormatter()
    {
        return formatter;
    }
}
