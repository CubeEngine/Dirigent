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

    private class StateHolder
    {
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
        boolean escape = false;
        int curPos = 0;

        public void start() // NONE -> START
        {
            state = State.START;
            posBuffer = new StringBuilder();
            typeBuffer = new StringBuilder();
            argsBuffer = null;
            typeArguments = null;
        }

        public void escaped(char curChar)
        {
            escape = false;
            finalString.append(curChar);
        }

        public void reset(char curChar)
        {
            state = State.NONE;
            finalString.append(MACRO_BEGIN).append(posBuffer);
            if (posBuffer.length() != 0 && typeBuffer.length() != 0)
            {
                finalString.append(MACRO_SEPARATOR);
            }
            finalString.append(typeBuffer).append(curChar);
        }

        public void format(Locale locale, Object[] messageArguments)
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
                List<Macro> macroList = formatters.get(type);
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
            Macro matched = compositor.matchMacroFor(messageArgument, defaultMacros);
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

        public void position(char curChar)
        {
            state = State.POS;
            posBuffer.append(curChar);
        }

        public void type(Character curChar)
        {
            state = State.TYPE;
            if (curChar != null)
            {
                typeBuffer.append(curChar);
            }
        }

        public void argumentStart()
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

        public void label()
        {
            state = State.LABEL;
        }

        public void argument(char curChar)
        {
            argsBuffer.append(curChar);
            escape = false;
        }
    }

    public final String composeMessage(String sourceMessage, Object... messageArgs)
    {
        return this.composeMessage(this.defaultLocale, sourceMessage, messageArgs);
    }

    public final String composeMessage(Locale locale, String sourceMessage, Object... messageArgs)
    {
        // {[[<position>:]type[#<label>][:<args>]]}
        char[] chars = sourceMessage.toCharArray();
        StateHolder holder = new StateHolder(this);
        for (char curChar : chars)
        {
            switch (holder.state)
            {
            case NONE:
                switch (curChar)
                {
                case MACRO_BEGIN:
                    if (holder.escape)
                    {
                        holder.escaped(curChar);
                        break;
                    }
                    holder.start();
                    break;
                case MACRO_ESCAPE:
                    if (holder.escape)
                    {
                        holder.escaped(curChar);
                        break;
                    }
                    holder.escape = true;
                    break;
                case MACRO_SEPARATOR:
                case MACRO_END:
                default:
                    if (holder.escape) // re-add escaping char
                    {
                        holder.escaped(MACRO_ESCAPE);
                    }
                    holder.finalString.append(curChar);
                    break;
                }
                break;
            case START:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                case MACRO_SEPARATOR:
                    holder.reset(curChar);
                    break;
                case MACRO_END:
                    holder.format(locale, messageArgs);
                    break;
                default: // expecting position OR type
                    if (Character.isDigit(curChar)) // pos
                    {
                        holder.position(curChar);
                    }
                    else if ((curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z')) // type
                    {
                        holder.type(curChar);
                    }
                    else
                    {
                        holder.reset(curChar);
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
                    holder.reset(curChar);
                    break;
                case MACRO_SEPARATOR:
                    holder.type(null);
                    break;
                case MACRO_END:
                    holder.format(locale, messageArgs);
                    break;
                default:
                    if (Character.isDigit(curChar)) // pos
                    {
                        holder.position(curChar);
                    }
                    else
                    {
                        holder.reset(curChar);
                    }
                    break;
                }
                break;
            case TYPE:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                    holder.reset(curChar);
                    break;
                case MACRO_SEPARATOR:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.reset(curChar);
                        break;
                    }
                    holder.argumentStart();
                    break;
                case MACRO_LABEL:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.reset(curChar);
                        break;
                    }
                    holder.label();
                    break;
                case MACRO_END:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.reset(curChar);
                        break;
                    }
                    holder.format(locale, messageArgs);
                    break;
                default:
                    if ((curChar >= 'a' && curChar <= 'z') ||
                        (curChar >= 'A' && curChar <= 'Z') ||
                        Character.isDigit(curChar))
                    {
                        holder.type(curChar);
                        break;
                    }
                    holder.reset(curChar);
                }
                break;
            case LABEL:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (holder.escape)
                    {
                        holder.escape = false;
                        break;
                    }
                    holder.escape = true;
                    break;
                case MACRO_SEPARATOR:
                    if (holder.escape)
                    {
                        holder.escape = false;
                        break;
                    }
                    holder.argumentStart();
                    break;
                case MACRO_END:
                    if (holder.escape)
                    {
                        holder.escape = false;
                        break;
                    }
                    holder.format(locale, messageArgs);
                    break;
                default:
                    holder.escape = false;
                }
                break;
            case ARGUMENTS:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (holder.escape) // "\\\\"
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.escape = true;
                case MACRO_SEPARATOR:
                    if (holder.escape)
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.argumentStart();
                    break;
                case MACRO_END:
                    if (holder.escape)
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.format(locale, messageArgs);
                    break;
                default:
                    holder.argument(curChar);
                }
                break;
            }
        }
        return holder.finalString.toString();
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
