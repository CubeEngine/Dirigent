/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.messagecompositor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.cubeisland.engine.messagecompositor.exception.MissingMacroException;
import de.cubeisland.engine.messagecompositor.macro.Formatter;
import de.cubeisland.engine.messagecompositor.macro.Macro;

class Message
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';
    public static final char MACRO_LABEL = '#';

    enum State
    {
        NONE, START, POS, TYPE, LABEL, ARGUMENTS
    }

    private final DefaultMessageCompositor compositor;
    private final Locale locale;
    private final String sourceMessage;
    private final Object[] messageArgs;

    private State state = State.NONE;
    StringBuilder finalString = new StringBuilder();
    StringBuilder posBuffer = new StringBuilder();
    StringBuilder typeBuffer = new StringBuilder();
    StringBuilder argsBuffer = null;
    List<String> typeArguments = null;
    private boolean escape = false;
    int curPos = 0;

    Message(DefaultMessageCompositor compositor, Locale locale, String sourceMessage, Object[] messageArgs)
    {
        this.compositor = compositor;
        this.locale = locale;
        this.sourceMessage = sourceMessage;
        this.messageArgs = messageArgs.clone();
    }

    /**
     * Processes the macros in the sourceMessage and returns the result
     *
     * @return the processed message
     */
    public final String process()
    {
        char[] chars = this.sourceMessage.toCharArray();
        for (char curChar : chars)
        {
            this.chooseState(curChar);
        }
        return this.finalString.toString();
    }

    // STATE-Methods

    private void chooseState(char curChar)
    {
        switch (this.state)
        {
        case NONE:
            this.stateNone(curChar);
            break;
        case START:
            this.stateStart(curChar);
            break;
        case POS:
            this.statePos(curChar);
            break;
        case TYPE:
            this.stateType(curChar);
            break;
        case LABEL:
            this.stateLabel(curChar);
            break;
        case ARGUMENTS:
            this.stateArguments(curChar);
            break;
        }
    }

    private void stateNone(char curChar)
    {
        switch (curChar)
        {
        case MACRO_BEGIN:
            if (this.escaped())
            {
                this.none(curChar);
                break;
            }
            this.startMacro();
            break;
        case MACRO_ESCAPE:
            if (this.escaped())
            {
                this.none(curChar);
                break;
            }
            this.escape();
            break;
        case MACRO_SEPARATOR:
        case MACRO_END:
        default:
            if (this.escaped()) // re-add escaping char
            {
                this.none(MACRO_ESCAPE);
            }
            this.none(curChar);
            break;
        }
    }

    private void stateStart(char curChar)
    {
        switch (curChar)
        {
        case MACRO_BEGIN:
        case MACRO_ESCAPE:
        case MACRO_SEPARATOR:
            this.resetMacro(curChar);
            break;
        case MACRO_END:
            this.format();
            break;
        default: // expecting position OR type
            if (Character.isDigit(curChar)) // pos
            {
                this.position(curChar);
                break;
            }
            if (this.isLetter(curChar)) // type
            {
                this.type(curChar);
                break;
            }
            this.resetMacro(curChar);
            break;
        }
    }

    private void statePos(char curChar)
    {
        switch (curChar)
        {
        case MACRO_BEGIN:
        case MACRO_ESCAPE:
            this.resetMacro(curChar);
            break;
        case MACRO_SEPARATOR:
            this.type(null);
            break;
        case MACRO_END:
            this.format();
            break;
        default:
            if (Character.isDigit(curChar)) // pos
            {
                this.position(curChar);
                break;
            }
            this.resetMacro(curChar);
            break;
        }
    }

    private void stateType(char curChar)
    {
        switch (curChar)
        {
        case MACRO_BEGIN:
        case MACRO_ESCAPE:
            this.resetMacro(curChar);
            break;
        case MACRO_SEPARATOR:
            if (this.typeBuffer.length() == 0)
            {
                this.resetMacro(curChar);
                break;
            }
            this.startArgument();
            break;
        case MACRO_LABEL:
            if (this.typeBuffer.length() == 0)
            {
                this.resetMacro(curChar);
                break;
            }
            this.label();
            break;
        case MACRO_END:
            if (this.typeBuffer.length() == 0)
            {
                this.resetMacro(curChar);
                break;
            }
            this.format();
            break;
        default:
            if (this.isLetter(curChar) || Character.isDigit(curChar))
            {
                this.type(curChar);
                break;
            }
            this.resetMacro(curChar);
            break;
        }
    }

    private void stateLabel(char curChar)
    {
        switch (curChar)
        {
        case MACRO_ESCAPE:
            if (this.escaped())
            {
                this.label();
                break;
            }
            this.escape();
            break;
        case MACRO_SEPARATOR:
            if (this.escaped())
            {
                this.label();
                break;
            }
            this.startArgument();
            break;
        case MACRO_END:
            if (this.escaped())
            {
                this.label();
                break;
            }
            this.format();
            break;
        default:
            this.label();
            break;
        }
    }

    private void stateArguments(char curChar)
    {
        switch (curChar)
        {
        case MACRO_ESCAPE:
            if (this.escaped()) // "\\\\"
            {
                this.argument(curChar);
                break;
            }
            this.escape();
            break;
        case MACRO_SEPARATOR:
            if (this.escaped())
            {
                this.argument(curChar);
                break;
            }
            this.startArgument();
            break;
        case MACRO_END:
            if (this.escaped())
            {
                this.argument(curChar);
                break;
            }
            this.format();
            break;
        default:
            this.argument(curChar);
            break;
        }
    }

    // Helper-Methods:

    private boolean isLetter(char character)
    {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    private boolean escaped()
    {
        return escape;
    }

    private void escape()
    {
        this.escape = true;
    }

    // NONE -> NONE
    private void none(char curChar)
    {
        escape = false;
        finalString.append(curChar);
    }

    // NONE -> START
    private void startMacro()
    {
        state = State.START;
        posBuffer = new StringBuilder();
        typeBuffer = new StringBuilder();
        argsBuffer = null;
        typeArguments = null;
    }

    // ? -> NONE
    private void resetMacro(char curChar)
    {
        state = State.NONE;
        finalString.append(MACRO_BEGIN).append(posBuffer);
        if (posBuffer.length() != 0 && typeBuffer.length() != 0)
        {
            finalString.append(MACRO_SEPARATOR);
        }
        finalString.append(typeBuffer).append(curChar);
    }

    // START -> POS
    private void position(char curChar)
    {
        state = State.POS;
        posBuffer.append(curChar);
    }

    // START|POS -> TYPE
    private void type(Character curChar)
    {
        state = State.TYPE;
        if (curChar != null)
        {
            typeBuffer.append(curChar);
        }
    }

    // TYPE|LABEL -> LABEL
    private void label()
    {
        state = State.LABEL;
        escape = false;
    }

    // TYPE|ARGUMENTS -> ARGUMENTS#next
    private void startArgument()
    {
        state = State.ARGUMENTS;
        if (typeArguments == null)
        {
            typeArguments = new ArrayList<String>();
        }
        if (argsBuffer != null)
        {
            typeArguments.add(argsBuffer.toString());
        }
        argsBuffer = new StringBuilder();
    }

    // ARGUMENTS -> ARGUMENTS
    private void argument(char curChar)
    {
        argsBuffer.append(curChar);
        escape = false;
    }

    // ? -> format -> NONE
    private void format()
    {
        if (argsBuffer != null)
        {
            typeArguments.add(argsBuffer.toString());
        }
        Integer manualPos = null;
        if (posBuffer.length() != 0)
        {
            manualPos = Integer.valueOf(posBuffer.toString());
        }
        final int pos = manualPos == null ? curPos : manualPos;
        final Object messageArgument = messageArgs.length > pos ? messageArgs[pos] : null;
        state = State.NONE;
        String type = typeBuffer.toString();
        if (!type.isEmpty())
        {
            List<Macro> macroList = compositor.macros.get(type);
            if (macroList != null)
            {
                Macro matched = compositor.matchMacroFor(messageArgument, macroList);
                if (matched != null)
                {
                    compositor.format(new MacroContext(compositor, matched, type, locale, typeArguments), messageArgument, finalString);
                    if (matched instanceof Formatter && manualPos == null)
                    {
                        curPos++;
                    }
                    return;
                }
            }
        }
        // else without type or not found:
        Macro matched = compositor.matchMacroFor(messageArgument, compositor.defaultMacros);
        if (matched != null)
        {
            compositor.format(new MacroContext(compositor, matched, null, locale, typeArguments), messageArgument, finalString);
            if (matched instanceof Formatter && manualPos == null)
            {
                curPos++;
            }
            return;
        }
        if (messageArgument == null)
        {
            throw new IllegalArgumentException("The message expected a messageArgument but could not find one");
        }
        if (type.isEmpty())
        {
            finalString.append(String.valueOf(messageArgument));
            curPos++;
            return;
        }
        throw new MissingMacroException(type, messageArgument.getClass());
    }
}
