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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.formatter.context.MacroContext;
import de.cubeisland.engine.formatter.formatter.ConstantMacro;
import de.cubeisland.engine.formatter.formatter.Formatter;
import de.cubeisland.engine.formatter.formatter.Macro;

public class DefaultMessageCompositor implements MessageCompositor
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';
    public static final char MACRO_LABEL = '#';

    private final Locale defaultLocale;

    public DefaultMessageCompositor()
    {
        this(Locale.getDefault());
    }

    public DefaultMessageCompositor(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    private enum State
    {
        NONE,
        START,
        POS,
        TYPE,
        LABEL,
        ARGUMENTS
    }

    public final String composeMessage(String sourceMessage, Object... messageArgs)
    {
        return this.composeMessage(this.defaultLocale, sourceMessage, messageArgs);
    }

    public final String composeMessage(Locale locale, String sourceMessage, Object... messageArgs)
    {
        State state = State.NONE;
        boolean escape = false;
        // {[[<position>:]type[:<args>]]}
        StringBuilder finalString = new StringBuilder();
        char[] chars = sourceMessage.toCharArray();
        int curPos = 0;

        StringBuilder posBuffer = new StringBuilder();
        StringBuilder typeBuffer = new StringBuilder();
        StringBuilder argsBuffer = new StringBuilder();
        List<String> typeArguments = null;

        for (char curChar : chars)
        {
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
                    posBuffer = new StringBuilder();
                    typeBuffer = new StringBuilder();
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
                    if (this.format(locale, null, null, messageArgs, curPos, curPos, finalString))
                    {
                        curPos++;
                    }
                    state = State.NONE;
                    break;
                default: // expecting position OR type
                    if (Character.isDigit(curChar)) // pos
                    {
                        state = State.POS;
                        posBuffer.append(curChar);
                    }
                    else if ((curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z')) // type
                    {
                        state = State.TYPE;
                        typeBuffer = new StringBuilder().append(curChar);
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
                    if (this.format(locale, null, null, messageArgs, curPos, Integer.valueOf(posBuffer.toString()), finalString))
                    {
                        curPos++;
                    }
                    state = State.NONE;
                    break;
                default:
                    if (Character.isDigit(curChar)) // pos
                    {
                        posBuffer.append(curChar);
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
                    finalString.append(MACRO_BEGIN).append(posBuffer).append(posBuffer.length() == 0 ? MACRO_SEPARATOR : "")
                               .append(typeBuffer).append(curChar);
                    break;
                case MACRO_SEPARATOR:
                    if (typeBuffer.length() == 0)
                    {
                        finalString.append(MACRO_BEGIN);
                        if (posBuffer.length() != 0)
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = new StringBuilder();
                        break;
                    }
                    state = State.ARGUMENTS;
                    typeArguments = new ArrayList<String>();
                    argsBuffer = new StringBuilder();
                    break;
                case MACRO_LABEL:
                    if (typeBuffer.length() == 0)
                    {
                        finalString.append(MACRO_BEGIN);
                        if (posBuffer.length() != 0)
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = new StringBuilder();
                        break;
                    }
                    state = State.LABEL;
                    break;
                case MACRO_END:
                    if (typeBuffer.length() == 0)
                    {
                        finalString.append(MACRO_BEGIN);
                        if (posBuffer.length() != 0)
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = new StringBuilder();
                        break;
                    }
                    int pos = curPos;
                    if (posBuffer.length() != 0)
                    {
                        pos = Integer.valueOf(posBuffer.toString()); // Specified arg pos, NO increment counting pos.
                    }
                    if (this.format(locale, typeBuffer.toString(), null, messageArgs, curPos, pos, finalString))
                    {
                        curPos++;
                    }
                    state = State.NONE;
                    break;
                default:
                    if ((curChar >= 'a' && curChar <= 'z') ||
                        (curChar >= 'A' && curChar <= 'Z') ||
                        Character.isDigit(curChar))
                    {
                        typeBuffer.append(curChar);
                    }
                    else
                    {
                        finalString.append(MACRO_BEGIN);
                        if (posBuffer.length() != 0)
                        {
                            finalString.append(posBuffer).append(MACRO_SEPARATOR);
                        }
                        finalString.append(curChar);
                        state = State.NONE;
                        posBuffer = new StringBuilder();
                        break;
                    }
                }
                break;
            case LABEL:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (escape)
                    {
                        escape = false;
                        break;
                    }
                    escape = true;
                    break;
                case MACRO_SEPARATOR:
                    if (escape)
                    {
                        escape = false;
                        break;
                    }
                    state = State.ARGUMENTS;
                    typeArguments = new ArrayList<String>();
                    argsBuffer = new StringBuilder();
                    break;
                case MACRO_END:
                    if (escape)
                    {
                        escape = false;
                        break;
                    }
                    int pos = curPos;
                    if (posBuffer.length() != 0)
                    {
                        pos = Integer.valueOf(posBuffer.toString()); // Specified arg pos, NO increment counting pos.
                    }
                    if (this.format(locale, typeBuffer.toString(), null, messageArgs, curPos, pos, finalString))
                    {
                        curPos++;
                    }
                    state = State.NONE;
                    break;
                default:
                    escape = false;
                }
                break;
            case ARGUMENTS:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (escape) // "\\\\"
                    {
                        escape = false;
                        argsBuffer.append(curChar);
                        break;
                    }
                    escape = true;
                case MACRO_SEPARATOR:
                    if (escape)
                    {
                        argsBuffer.append(curChar);
                        escape = false;
                        break;
                    }
                    typeArguments.add(argsBuffer.toString());
                    argsBuffer = new StringBuilder(); // Next Flag...
                    break;
                case MACRO_END:
                    if (escape)
                    {
                        argsBuffer.append(curChar);
                        escape = false;
                    }
                    else
                    {
                        int pos = curPos;
                        if (posBuffer.length() != 0)
                        {
                            pos = Integer.valueOf(posBuffer.toString()); // Specified arg pos, NO increment counting pos.
                        }
                        typeArguments.add(argsBuffer.toString());
                        if (this.format(locale, typeBuffer.toString(), typeArguments, messageArgs, curPos, pos, finalString))
                        {
                            curPos++;
                        }
                        state = State.NONE;
                    }
                    break;
                default:
                    argsBuffer.append(curChar);
                    escape = false;
                }
                break;
            }
        }
        return finalString.toString();
    }

    private Map<String, List<Macro>> formatters = new HashMap<String, List<Macro>>();
    private Set<Macro> defaultMacros = new HashSet<Macro>();

    public MessageCompositor registerMacro(Macro macro)
    {
        return this.registerMacro(macro, false);
    }

    public MessageCompositor registerMacro(Macro macro, boolean asDefault)
    {
        if (asDefault)
        {
            this.registerDefaultMacro(macro);
        }
        for (String name : macro.names())
        {
            List<Macro> list = this.formatters.get(name);
            if (list == null)
            {
                this.formatters.put(name, list = new ArrayList<Macro>());
            }
            list.add(macro);
        }
        return this;
    }

    public MessageCompositor registerDefaultMacro(Macro macro)
    {
        this.defaultMacros.add(macro);
        return this;
    }

    // override in CE to append color at the end of format

    private boolean format(Locale locale, String type, List<String> typeArguments, Object[] messageArguments, int curPos, int actualPos, StringBuilder finalString)
    {
        Object messageArgument = null;
        if (messageArguments.length > actualPos)
        {
            messageArgument = messageArguments[actualPos];
        }
        if (type != null)
        {
            List<Macro> macroList = this.formatters.get(type);
            if (macroList != null)
            {
                Macro matched = this.matchMacroFor(messageArgument, macroList);
                if (matched != null)
                {
                    this.format(new MacroContext(matched, type, locale, typeArguments), messageArgument, finalString);
                    return matched instanceof Formatter && curPos == actualPos;
                }
            }
        }
        // else without type or not found:
        Macro matched = this.matchMacroFor(messageArgument, defaultMacros);
        if (matched != null)
        {
            this.format(new MacroContext(matched, null, locale, typeArguments), messageArgument, finalString);
            return matched instanceof Formatter && curPos == actualPos;
        }
        if (messageArgument == null)
        {
            throw new IllegalArgumentException(); // TODO msg
        }
        if (type == null)
        {
            finalString.append(String.valueOf(messageArgument));
            return true;
        }
        throw new MissingFormatterException(type, messageArgument.getClass());
    }

    @SuppressWarnings("unchecked")
    private Macro matchMacroFor(Object messageArgument, Collection<Macro> macroList)
    {
        ConstantMacro cMacro = null;
        for (Macro macro : macroList)
        {
            if (macro instanceof ConstantMacro)
            {
                cMacro = (ConstantMacro)macro;
            }
            else if (messageArgument != null && macro instanceof Formatter && ((Formatter)macro).isApplicable(messageArgument.getClass()))
            {
                return macro;
            }
        }
        return cMacro;
    }

    @SuppressWarnings("unchecked")
    private void format(MacroContext context, Object messageArguments, StringBuilder finalString)
    {
        this.preFormat(context, messageArguments, finalString);
        Macro macro = context.getMacro();
        if (macro instanceof Formatter)
        {
            finalString.append(((Formatter)macro).process(messageArguments, context));
        }
        else if (macro instanceof ConstantMacro)
        {
            finalString.append(((ConstantMacro)macro).process(context));
        }
        else
        {
            throw new IllegalArgumentException("Unknown Macro! " + macro.getClass().getName());
        }
        this.postFormat(context, messageArguments, finalString);
    }

    public void postFormat(MacroContext context, Object messageArgument, StringBuilder finalString)
    {

    }

    public void preFormat(MacroContext context, Object messageArgument, StringBuilder finalString)
    {

    }
}
