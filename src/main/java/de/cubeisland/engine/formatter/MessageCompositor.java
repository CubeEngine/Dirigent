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

public class MessageCompositor
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';

    private enum State
    {
        NONE,
        START,
        POS,
        TYPE,
        FLAGS
    }

    public String composeMessage(String sourceMessage, Object... args)
    {
        State state = State.NONE;
        boolean escape = false;
        // {[[<position>:]type[:<flags>]]}
        StringBuilder finalString = new StringBuilder();
        char[] chars = sourceMessage.toCharArray();
        int curPos = 0;

        String posBuffer = "";
        String typeBuffer = "";
        String flagsBuffer = "";
        List<String> flags = null;

        for (int i = 0; i < chars.length; i++)
        {
            char curChar = chars[i];
            switch (state)
            {
            case NONE:
                switch (curChar)
                {
                case MACRO_BEGIN:
                    if (escape)
                    {
                        escape = false;
                        finalString.append(curChar);
                        break;
                    }
                    state = State.START;
                    posBuffer = "";
                    typeBuffer = "";
                    break;
                case MACRO_ESCAPE:
                    if (escape)
                    {
                        finalString.append(curChar);
                        escape = false;
                    }
                    else
                    {
                        escape = true;
                    }
                    break;
                case MACRO_SEPARATOR:
                case MACRO_END:
                default:
                    if (escape)
                    {
                        escape = false;
                        finalString.append(MACRO_ESCAPE);
                    }
                    finalString.append(curChar);
                    break;
                }
                break;
            case START:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                case MACRO_SEPARATOR:
                    state = State.NONE;
                    finalString.append(MACRO_BEGIN).append(curChar);
                    break;
                // TODO OR this? throw new IllegalStateException("Illegal start of expression!");
                case MACRO_END:
                    // TODO do we want simple String replace ? {} -> String.valueOf(arg[curArg])
                    finalString.append(String.valueOf(args[curPos]));
                    curPos++;
                    state = State.NONE;
                    break;
                default: // expecting position OR type
                    if (Character.isDigit(curChar)) // pos
                    {
                        state = State.POS;
                        posBuffer += curChar;
                    }
                    else if ((curChar >= 'a' && curChar <= 'z') ||
                             (curChar >= 'A' && curChar <= 'Z')) // type
                    {
                        state = State.TYPE;
                        typeBuffer = "" + curChar;
                    }
                    else
                    {
                        state = State.NONE;
                        finalString.append(MACRO_BEGIN).append(curChar);
                        break;
                        // TODO OR this? throw new IllegalStateException("Illegal start of expression! Position or Type is expected!");
                    }
                    break;
                }
                break;
            case POS:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                    state = State.NONE;
                    finalString.append(MACRO_BEGIN).append(posBuffer).append(curChar);
                    // TODO OR this? throw new IllegalStateException("A position cannot contain non-numbers!");
                    break;
                case MACRO_SEPARATOR:
                    state = State.TYPE;
                    break;
                case MACRO_END:
                    // TODO do we want simple String replace ? {<number>} -> String.valueOf(arg[curArg])
                    finalString.append(String.valueOf(args[Integer.valueOf(posBuffer)]));
                    state = State.NONE;
                    break;
                default:
                    if (Character.isDigit(curChar)) // pos
                    {
                        posBuffer += curChar;
                    }
                    else
                    {
                        state = State.NONE;
                        finalString.append(MACRO_BEGIN).append(posBuffer).append(curChar);
                        // TODO OR this? throw new IllegalStateException("A position cannot contain non-numbers!");
                    }
                    break;
                }
                break;
            case TYPE:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                    state = State.NONE;
                    finalString.append(MACRO_BEGIN).append(posBuffer).append(posBuffer.isEmpty() ? MACRO_SEPARATOR : "")
                               .append(typeBuffer).append(curChar);
                    // TODO OR this? throw new IllegalStateException("A position cannot contain non-numbers!");
                    break;
                case MACRO_SEPARATOR:
                    if (typeBuffer.isEmpty())
                    {
                        throw new IllegalStateException("Empty Type"); // TODO or go to none and print stuff from before
                    }
                    state = State.FLAGS;
                    flags = new ArrayList<String>();
                    flagsBuffer = "";
                    break;
                case MACRO_END:
                    if (typeBuffer.isEmpty())
                    {
                        throw new IllegalStateException("Empty Type"); // TODO or go to none and print stuff from before
                    }
                    int pos = curPos;
                    if (posBuffer.isEmpty())
                    {
                        curPos++; // No specified arg pos, increment counting pos...
                    }
                    else
                    {
                        pos = Integer.valueOf(posBuffer); // Specified arg pos, NO increment counting pos.
                    }
                    // TODO message coloring before & after formatted object
                    finalString.append(this.format(typeBuffer, null, args[pos]));
                    state = State.NONE;
                    break;
                default:
                    if ((curChar >= 'a' && curChar <= 'z') ||
                        (curChar >= 'A' && curChar <= 'Z') ||
                        Character.isDigit(curChar))
                    {
                        typeBuffer += curChar;
                    }
                    else
                    {
                        throw new IllegalStateException("Only letters a-z A-Z and numbers 0-9 are allowed for type-names");
                    }
                }
                break;
            case FLAGS:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (escape) // "\\\\"
                    {
                        escape = false;
                        flagsBuffer += curChar;
                        break;
                    }
                    escape = true;
                case MACRO_SEPARATOR:
                    if (escape)
                    {
                        flagsBuffer += curChar;
                        break;
                    }
                    flags.add(flagsBuffer);
                    flagsBuffer = ""; // Next Flag...
                    break;
                case MACRO_END:
                    if (escape)
                    {
                        flagsBuffer += curChar;
                    }
                    else
                    {
                        int pos = curPos;
                        if (posBuffer.isEmpty())
                        {
                            curPos++; // No specified arg pos, increment counting pos...
                        }
                        else
                        {
                            pos = Integer.valueOf(posBuffer); // Specified arg pos, NO increment counting pos.
                        }
                        // TODO message coloring before & after formatted object
                        flags.add(flagsBuffer);
                        finalString.append(this.format(typeBuffer, flags, args[pos]));
                        state = State.NONE;
                    }
                    break;
                default:
                    flagsBuffer += curChar;
                }
                break;
            }
        }
        return finalString.toString();
    }

    private String format(String type, List<String> flags, Object arg)
    {
        // TODO find & use the formatters
        // TODO generate FormatContext for given flags
        System.out.println(type + " ; " + String.valueOf(flags) + " ; " + String.valueOf(arg));
        return String.valueOf(arg);
    }
}
