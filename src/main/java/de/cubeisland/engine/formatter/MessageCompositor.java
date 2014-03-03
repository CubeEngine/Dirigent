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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.formatter.context.FormatContext;
import de.cubeisland.engine.formatter.formatter.ArgumentSuffix;
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
        ARGUMENTS
    }

    public final String composeMessage(String sourceMessage, Object... messageArgs)
    {
        return this.composeMessage(Locale.getDefault(), null, sourceMessage, messageArgs);
    }

    public final String composeMessage(ArgumentSuffix suffix, String sourceMessage, Object... messagArgs)
    {
        return this.composeMessage(Locale.getDefault(), suffix, sourceMessage, messagArgs);
    }

    public final String composeMessage(Locale locale, ArgumentSuffix suffix, String sourceMessage, Object... messageArgs)
    {
        System.out.println(sourceMessage);
        State state = State.NONE;
        boolean escape = false;
        // {[[<position>:]type[:<args>]]}
        StringBuilder finalString = new StringBuilder();
        char[] chars = sourceMessage.toCharArray();
        int curPos = 0;

        String posBuffer = "";
        String typeBuffer = "";
        String argsBuffer = "";
        List<String> typeArguments = null;

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
                    finalString.append(this.format(locale, null, null, messageArgs[curPos], suffix));
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
                    finalString.append(this.format(locale, null, null, messageArgs[Integer.valueOf(posBuffer)], suffix));
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
                    state = State.ARGUMENTS;
                    typeArguments = new ArrayList<String>();
                    argsBuffer = "";
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
                    finalString.append(this.format(locale, typeBuffer, null, messageArgs[pos], suffix));
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
            case ARGUMENTS:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (escape) // "\\\\"
                    {
                        escape = false;
                        argsBuffer += curChar;
                        break;
                    }
                    escape = true;
                case MACRO_SEPARATOR:
                    if (escape)
                    {
                        argsBuffer += curChar;
                        break;
                    }
                    typeArguments.add(argsBuffer);
                    argsBuffer = ""; // Next Flag...
                    break;
                case MACRO_END:
                    if (escape)
                    {
                        argsBuffer += curChar;
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
                        typeArguments.add(argsBuffer);
                        finalString.append(this.format(locale, typeBuffer, typeArguments, messageArgs[pos], suffix));
                        state = State.NONE;
                    }
                    break;
                default:
                    argsBuffer += curChar;
                }
                break;
            }
        }
        return finalString.toString();
    }

    private Map<String, List<Formatter>> formatters = new HashMap<String, List<Formatter>>();
    private Set<Formatter> defaultFormatters = new HashSet<Formatter>();

    public void registerFormatter(Formatter<?> formatter)
    {
        for (String name : formatter.names())
        {
            List<Formatter> list = this.formatters.get(name);
            if (list == null)
            {
                this.formatters.put(name, list = new ArrayList<Formatter>());
            }
            list.add(formatter);
        }
    }

    public void registerDefaulFormatter(Formatter formatter)
    {
        this.defaultFormatters.add(formatter);
    }

    // override in CE to append color at the end of format
    private final String format(Locale locale, String type, List<String> typeArguments, Object messageArgument, ArgumentSuffix suffix)
    {
        if (type == null)
        {
            for (Formatter formatter : defaultFormatters)
            {
                if (formatter.isApplicable(messageArgument.getClass()))
                {
                    return this.format(formatter, FormatContext.of(formatter, locale, typeArguments), messageArgument, suffix);
                }
            }
            return String.valueOf(messageArgument);
        }
        List<Formatter> formatterList = this.formatters.get(type);
        if (formatterList != null)
        {
            for (Formatter formatter : formatterList)
            {
                if (formatter.isApplicable(messageArgument.getClass()))
                {
                    return this.format(formatter, FormatContext.of(formatter, locale, typeArguments), messageArgument, suffix);
                }
            }
        }
        for (Formatter formatter : defaultFormatters)
        {
            if (formatter.isApplicable(messageArgument.getClass()))
            {
                return this.format(formatter, FormatContext.of(formatter, locale, typeArguments), messageArgument, suffix);
            }
        }
        throw new MissingFormatterException(type, messageArgument.getClass());
    }

    protected String format(Formatter formatter, FormatContext context, Object messageArgument, ArgumentSuffix suffix)
    {
        return formatter.format(messageArgument, context) + (suffix == null ? "" : suffix.getSuffix());
    }
}
