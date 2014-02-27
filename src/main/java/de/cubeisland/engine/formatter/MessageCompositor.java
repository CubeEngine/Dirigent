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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.formatter.context.FormatContext;
import de.cubeisland.engine.formatter.formatter.Formatter;

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

    public final String composeMessage(String sourceMessage, Object... args)
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
                case MACRO_END:
                    finalString.append(this.format(null, null, args[curPos]));
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
                    break;
                case MACRO_SEPARATOR:
                    state = State.TYPE;
                    break;
                case MACRO_END:
                    finalString.append(this.format(null, null, args[Integer.valueOf(posBuffer)]));
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
                    break;
                case MACRO_SEPARATOR:
                    if (typeBuffer.isEmpty())
                    {
                        finalString.append(MACRO_BEGIN);
                        if (!posBuffer.isEmpty())
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = "";
                        break;
                    }
                    state = State.FLAGS;
                    flags = new ArrayList<String>();
                    flagsBuffer = "";
                    break;
                case MACRO_END:
                    if (typeBuffer.isEmpty())
                    {
                        finalString.append(MACRO_BEGIN);
                        if (!posBuffer.isEmpty())
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = "";
                        break;
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
                        finalString.append(MACRO_BEGIN);
                        if (!posBuffer.isEmpty())
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = "";
                        break;
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

    private Map<String, List<Formatter>> formatters = new HashMap<String, List<Formatter>>();
    private Set<Formatter> defaultFormatters = new HashSet<Formatter>();

    // override in CE to append color at the end of format
    protected String format(String type, List<String> flags, Object arg)
    {
        if (type == null)
        {
            for (Formatter formatter : defaultFormatters)
            {
                if (formatter.isApplicable(arg.getClass()))
                {
                    return formatter.format(arg, FormatContext.of(formatter, flags));
                }
            }
            return String.valueOf(arg);
        }
        List<Formatter> formatterList = this.formatters.get(type);
        if (formatterList != null)
        {
            for (Formatter formatter : formatterList)
            {
                if (formatter.isApplicable(arg.getClass()))
                {
                    System.out.println(type + " ; " + String.valueOf(flags) + " ; " + String.valueOf(arg));
                    return formatter.format(arg, FormatContext.of(formatter, flags));
                }
            }
        }
        for (Formatter formatter : defaultFormatters)
        {
            if (formatter.isApplicable(arg.getClass()))
            {
                System.out.println(type + " ; " + String.valueOf(flags) + " ; " + String.valueOf(arg));
                return formatter.format(arg, FormatContext.of(formatter, flags));
            }
        }
        throw new MissingFormatterException(type, arg.getClass());
    }
}
