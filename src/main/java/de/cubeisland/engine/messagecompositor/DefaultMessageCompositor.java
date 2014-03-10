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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.messagecompositor.context.MacroContext;
import de.cubeisland.engine.messagecompositor.context.Reader;
import de.cubeisland.engine.messagecompositor.macro.ConstantMacro;
import de.cubeisland.engine.messagecompositor.macro.Formatter;
import de.cubeisland.engine.messagecompositor.macro.Macro;

public class DefaultMessageCompositor implements MessageCompositor
{
    private final Locale defaultLocale;
    Map<String, List<Macro>> formatters = new HashMap<String, List<Macro>>();
    Set<Macro> defaultMacros = new HashSet<Macro>();

    private Map<String, Reader> readers = new HashMap<String, Reader>();
    private Map<Class<? extends Macro>, Map<String, Reader>> mappedReaders = new HashMap<Class<? extends Macro>, Map<String, Reader>>();
    private Map<Class<? extends Macro>, Reader> defaultReaders = new HashMap<Class<? extends Macro>, Reader>();

    public DefaultMessageCompositor()
    {
        this(Locale.getDefault());
    }

    public DefaultMessageCompositor(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    public final String composeMessage(String sourceMessage, Object... messageArguments)
    {
        return this.composeMessage(this.defaultLocale, sourceMessage, messageArguments);
    }

    public final String composeMessage(Locale locale, String sourceMessage, Object... messageArguments)
    {

        char[] chars = sourceMessage.toCharArray();
        StateHolder holder = new StateHolder(this, locale, sourceMessage, messageArguments);
        for (char curChar : chars)
        {
            holder.chooseState(curChar);
        }
        return holder.finalString.toString();
    }

    public final MessageCompositor registerMacro(Macro macro)
    {
        return this.registerMacro(macro, false);
    }

    public final MessageCompositor registerMacro(Macro macro, boolean asDefault)
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

    public final MessageCompositor registerDefaultMacro(Macro macro)
    {
        this.defaultMacros.add(macro);
        return this;
    }

    public MessageCompositor registerReader(String key, Reader reader)
    {
        readers.put(key, reader);
        return this;
    }

    public MessageCompositor registerReader(Class<? extends Macro> macroClass, String key, Reader reader)
    {
        Map<String, Reader> readers = mappedReaders.get(macroClass);
        if (readers == null)
        {
            mappedReaders.put(macroClass, readers = new HashMap<String, Reader>());
        }
        readers.put(key, reader);
        return this;
    }

    public MessageCompositor registerDefaultReader(Class<? extends Macro> macroClass, Reader reader)
    {
        this.defaultReaders.put(macroClass, reader);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T read(String key, String value, Class<T> clazz)
    {
        Reader reader = this.readers.get(key);
        if (reader == null)
        {
            return null;
        }
        return (T)reader.getData(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(Macro macro, String key, String value, Class<T> clazz)
    {
        Map<String, Reader> stringReaderMap = this.mappedReaders.get(macro.getClass());
        if (stringReaderMap == null)
        {
            return null;
        }
        Reader reader = stringReaderMap.get(key);
        if (reader == null)
        {
            return null;
        }
        return (T)reader.getData(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(Macro macro, String value, Class<T> clazz)
    {
        Reader reader = this.defaultReaders.get(macro.getClass());
        if (reader == null)
        {
            return null;
        }
        return (T)reader.getData(value);
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
