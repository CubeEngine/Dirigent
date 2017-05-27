package org.cubeengine.dirigent.parser;

/**
 * Describes the state of the macro resolution result.
 */
public enum MacroResolutionState
{
    /**
     * The resolution succeeded.
     */
    OK,
    /**
     * No formatter for the given name was found.
     */
    UNKNOWN_NAME,
    /**
     * Formatters were found, but none of them were applicable to the given input.
     */
    NONE_APPLICABLE
}
