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
                    if (holder.escaped())
                    {
                        holder.none(curChar);
                        break;
                    }
                    holder.startMacro();
                    break;
                case MACRO_ESCAPE:
                    if (holder.escaped())
                    {
                        holder.none(curChar);
                        break;
                    }
                    holder.escape();
                    break;
                case MACRO_SEPARATOR:
                case MACRO_END:
                default:
                    if (holder.escaped()) // re-add escaping char
                    {
                        holder.none(MACRO_ESCAPE);
                    }
                    holder.none(curChar);
                    break;
                }
                break;
            case START:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                case MACRO_SEPARATOR:
                    holder.resetMacro(curChar);
                    break;
                case MACRO_END:
                    holder.format(locale, messageArgs);
                    break;
                default: // expecting position OR type
                    if (Character.isDigit(curChar)) // pos
                    {
                        holder.position(curChar);
                        break;
                    }
                    if ((curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z')) // type
                    {
                        holder.type(curChar);
                        break;
                    }
                    holder.resetMacro(curChar);
                    break;
                }
                break;
            case POS:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                    holder.resetMacro(curChar);
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
                        break;
                    }
                    holder.resetMacro(curChar);
                    break;
                }
                break;
            case TYPE:
                switch (curChar)
                {
                case MACRO_BEGIN:
                case MACRO_ESCAPE:
                    holder.resetMacro(curChar);
                    break;
                case MACRO_SEPARATOR:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.resetMacro(curChar);
                        break;
                    }
                    holder.startArgument();
                    break;
                case MACRO_LABEL:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.resetMacro(curChar);
                        break;
                    }
                    holder.label();
                    break;
                case MACRO_END:
                    if (holder.typeBuffer.length() == 0)
                    {
                        holder.resetMacro(curChar);
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
                    holder.resetMacro(curChar);
                    break;
                }
                break;
            case LABEL:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (holder.escaped())
                    {
                        holder.label();
                        break;
                    }
                    holder.escape();
                    break;
                case MACRO_SEPARATOR:
                    if (holder.escaped())
                    {
                        holder.label();
                        break;
                    }
                    holder.startArgument();
                    break;
                case MACRO_END:
                    if (holder.escaped())
                    {
                        holder.label();
                        break;
                    }
                    holder.format(locale, messageArgs);
                    break;
                default:
                    holder.label();
                    break;
                }
                break;
            case ARGUMENTS:
                switch (curChar)
                {
                case MACRO_ESCAPE:
                    if (holder.escaped()) // "\\\\"
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.escape();
                    break;
                case MACRO_SEPARATOR:
                    if (holder.escaped())
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.startArgument();
                    break;
                case MACRO_END:
                    if (holder.escaped())
                    {
                        holder.argument(curChar);
                        break;
                    }
                    holder.format(locale, messageArgs);
                    break;
                default:
                    holder.argument(curChar);
                    break;
                }
                break;
            }
        }
        return holder.finalString.toString();
    }

    Map<String, List<Macro>> formatters = new HashMap<String, List<Macro>>();
    Set<Macro> defaultMacros = new HashSet<Macro>();

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

    @SuppressWarnings("unchecked")
    Macro matchMacroFor(Object messageArgument, Collection<Macro> macroList)
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
    void format(MacroContext context, Object messageArguments, StringBuilder finalString)
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
