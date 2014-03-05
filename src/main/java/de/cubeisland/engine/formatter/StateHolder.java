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
package de.cubeisland.engine.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.cubeisland.engine.formatter.context.MacroContext;
import de.cubeisland.engine.formatter.formatter.Formatter;
import de.cubeisland.engine.formatter.formatter.Macro;

class StateHolder
{
    enum State
    {
        NONE,
        START,
        POS,
        TYPE,
        LABEL,
        ARGUMENTS
    }

    private final DefaultMessageCompositor compositor;

    StateHolder(DefaultMessageCompositor compositor)
    {
        this.compositor = compositor;
    }

    State state = State.NONE;

    StringBuilder finalString = new StringBuilder();

    StringBuilder posBuffer = new StringBuilder();
    StringBuilder typeBuffer = new StringBuilder();
    StringBuilder argsBuffer = null;
    List<String> typeArguments = null;
    private boolean escape = false;
    int curPos = 0;

    public boolean escaped()
    {
        return escape;
    }

    public void escape()
    {
        this.escape = true;
    }

    public void none(char curChar) // NONE -> NONE
    {
        escape = false;
        finalString.append(curChar);
    }

    public void startMacro() // NONE -> START
    {
        state = State.START;
        posBuffer = new StringBuilder();
        typeBuffer = new StringBuilder();
        argsBuffer = null;
        typeArguments = null;
    }

    public void resetMacro(char curChar) // ? -> NONE
    {
        state = State.NONE;
        finalString.append(DefaultMessageCompositor.MACRO_BEGIN).append(posBuffer);
        if (posBuffer.length() != 0 && typeBuffer.length() != 0)
        {
            finalString.append(DefaultMessageCompositor.MACRO_SEPARATOR);
        }
        finalString.append(typeBuffer).append(curChar);
    }

    public void position(char curChar) // START -> POS
    {
        state = State.POS;
        posBuffer.append(curChar);
    }

    public void type(Character curChar) // START|POS -> TYPE
    {
        state = State.TYPE;
        if (curChar != null)
        {
            typeBuffer.append(curChar);
        }
    }

    public void label() // TYPE|LABEL -> LABEL
    {
        state = State.LABEL;
        escape = false;
    }

    public void startArgument() // TYPE|ARGUMENTS -> ARGUMENTS#next
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

    public void argument(char curChar) // ARGUMENTS -> ARGUMENTS
    {
        argsBuffer.append(curChar);
        escape = false;
    }

    public void format(Locale locale, Object[] messageArguments) // ? -> format -> NONE
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
        final Object messageArgument = messageArguments.length > pos ? messageArguments[pos] : null;
        state = State.NONE;
        String type = typeBuffer.toString();
        if (!type.isEmpty())
        {
            List<Macro> macroList = compositor.formatters.get(type);
            if (macroList != null)
            {
                Macro matched = compositor.matchMacroFor(messageArgument, macroList);
                if (matched != null)
                {
                    compositor.format(new MacroContext(matched, type, locale, typeArguments), messageArgument, finalString);
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
            compositor.format(new MacroContext(matched, null, locale, typeArguments), messageArgument, finalString);
            if (matched instanceof Formatter && manualPos == null)
            {
                curPos++;
            }
            return;
        }
        if (messageArgument == null)
        {
            throw new IllegalArgumentException(); // TODO msg
        }
        if (type.isEmpty())
        {
            finalString.append(String.valueOf(messageArgument));
            curPos++;
            return;
        }
        throw new MissingFormatterException(type, messageArgument.getClass());
    }
}
