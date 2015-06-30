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
package de.cubeisland.engine.messagecompositor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import de.cubeisland.engine.messagecompositor.exception.MissingMacroException;
import de.cubeisland.engine.messagecompositor.macro.Formatter;
import de.cubeisland.engine.messagecompositor.macro.Macro;
import de.cubeisland.engine.messagecompositor.macro.MacroContext;

class OldMessage
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';
    public static final char MACRO_LABEL = '#';

    private enum State
    {
        NONE,
        START,
        POS,
        TYPE,
        LABEL,
        ARGUMENTS
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

    OldMessage(DefaultMessageCompositor compositor, Locale locale, String sourceMessage, Object[] messageArgs)
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
        if (this.state == State.NONE)
        {
            this.stateNone(curChar);
        }
        else if (this.state == State.START)
        {
            this.stateStart(curChar);
        }
        else if (this.state == State.POS)
        {
            this.statePos(curChar);
        }
        else if (this.state == State.TYPE)
        {
            this.stateType(curChar);
        }
        else if (this.state == State.LABEL)
        {
            this.stateLabel(curChar);
        }
        else if (this.state == State.ARGUMENTS)
        {
            this.stateArguments(curChar);
        }
    }

    private void stateNone(char curChar)
    {
        if (this.escaped())
        {
            if (curChar != MACRO_BEGIN && curChar != MACRO_ESCAPE)
            {
                this.none(MACRO_ESCAPE);
            }
            this.none(curChar);
        }
        else if (curChar == MACRO_BEGIN)
        {
            this.startMacro();
        }
        else if (curChar == MACRO_ESCAPE)
        {
            this.escape();
        }
        else
        {
            this.none(curChar);
        }
    }

    private void stateStart(char curChar)
    {
        if (curChar == MACRO_BEGIN || curChar == MACRO_ESCAPE || curChar == MACRO_SEPARATOR)
        {
            this.resetMacro(curChar);
        }
        else if (curChar == MACRO_END)
        {
            this.format();
        }
        else if (Character.isDigit(curChar))
        {
            this.position(curChar);
        }
        else if (this.isLetter(curChar))
        {
            this.type(curChar);
        }
        else
        {
            this.resetMacro(curChar);
        }
    }

    private void statePos(char curChar)
    {
        if (curChar == MACRO_BEGIN || curChar == MACRO_ESCAPE)
        {
            this.resetMacro(curChar);
        }
        else if (curChar == MACRO_SEPARATOR)
        {
            this.type(null);
        }
        else if (curChar == MACRO_END)
        {
            this.format();
        }
        else if (Character.isDigit(curChar)) // pos
        {
            this.position(curChar);
        }
        else
        {
            this.resetMacro(curChar);
        }
    }

    private void stateType(char curChar)
    {
        if (typeBuffer.length() == 0)
        {
            if (curChar == MACRO_SEPARATOR || curChar == MACRO_LABEL || curChar == MACRO_END)
            {
                this.resetMacro(curChar);
                return;
            }
        }
        if (curChar == MACRO_BEGIN || curChar == MACRO_ESCAPE)
        {
            this.resetMacro(curChar);
        }
        else if (curChar == MACRO_SEPARATOR)
        {
            this.startArgument();
        }
        else if (curChar == MACRO_LABEL)
        {
            this.label();
        }
        else if (curChar == MACRO_END)
        {
            this.format();
        }
        else if (this.isLetter(curChar) || Character.isDigit(curChar))
        {
            this.type(curChar);
        }
        else
        {
            this.resetMacro(curChar);
        }
    }

    private void stateLabel(char curChar)
    {
        if (this.escaped())
        {
            // ignore what got escaped, as the label is not read
            this.label();
        }
        else if (curChar == MACRO_ESCAPE)
        {
            this.escape();
        }
        else if (curChar == MACRO_SEPARATOR)
        {
            this.startArgument();
        }
        else if (curChar == MACRO_END)
        {
            this.format();
        }
        else
        {
            this.label();
        }
    }

    private void stateArguments(char curChar)
    {
        if (this.escaped())
        {
            if (curChar == MACRO_ESCAPE || curChar == MACRO_SEPARATOR || curChar == MACRO_END)
            {
                this.argument(curChar);
                return;
            }
            this.argument(MACRO_ESCAPE);
            this.argument(curChar);
        }
        else if (curChar == MACRO_ESCAPE)
        {
            this.escape();
        }
        else if (curChar == MACRO_SEPARATOR)
        {
            this.startArgument();
        }
        else if (curChar == MACRO_END)
        {
            this.format();
        }
        else
        {
            this.argument(curChar);
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
        this.endArgument();
        argsBuffer = new StringBuilder();
    }

    private void endArgument()
    {
        if (argsBuffer != null)
        {
            typeArguments.add(argsBuffer.toString());
        }
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
        this.endArgument();
        Integer manualPos = null;
        if (posBuffer.length() != 0)
        {
            manualPos = Integer.valueOf(posBuffer.toString());
        }
        final int pos = manualPos == null ? curPos : manualPos;
        final Object messageArgument = messageArgs.length > pos ? messageArgs[pos] : null;
        state = State.NONE;
        String type = typeBuffer.toString();
        Macro matched = null;
        if (!type.isEmpty())
        {
            matched = compositor.matchMacroFor(messageArgument, type);
        }
        if (matched == null)
        {
            matched = compositor.matchMacroFor(messageArgument, compositor.defaultMacros);
        }
        if (matched != null)
        {
            MacroContext ctx = new MacroContext(sourceMessage, compositor, matched, type, locale, typeArguments);
            finalString.append(compositor.format(ctx, messageArgument));
            this.adjustCurPos(matched, manualPos);
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

    private void adjustCurPos(Macro matched, Integer manualPos)
    {
        if (matched instanceof Formatter && manualPos == null)
        {
            curPos++;
        }
    }
}
