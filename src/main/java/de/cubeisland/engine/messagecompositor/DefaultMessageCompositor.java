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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.messagecompositor.macro.ConstantMacro;
import de.cubeisland.engine.messagecompositor.macro.Formatter;
import de.cubeisland.engine.messagecompositor.macro.Macro;
import de.cubeisland.engine.messagecompositor.macro.MacroContext;
import de.cubeisland.engine.messagecompositor.macro.PostProcessor;
import de.cubeisland.engine.messagecompositor.macro.Reader;

public class DefaultMessageCompositor implements MessageCompositor
{
    private final Locale defaultLocale;

    Map<String, List<Macro>> macros = new HashMap<String, List<Macro>>();
    Set<Macro> defaultMacros = new HashSet<Macro>();

    private Map<String, Reader> readers = new HashMap<String, Reader>();
    private Map<Class<? extends Macro>, Map<String, Reader>> mappedReaders = new HashMap<Class<? extends Macro>, Map<String, Reader>>();
    private Map<Class<? extends Macro>, Reader> defaultReaders = new HashMap<Class<? extends Macro>, Reader>();
    private LinkedHashSet<PostProcessor> defaultPostProcessors = new LinkedHashSet<PostProcessor>();

    /**
     * Creates a MessageCompositor with the default locale
     */
    public DefaultMessageCompositor()
    {
        this(Locale.getDefault());
    }

    /**
     * Creates a MessageCompositor with a set default locale
     *
     * @param defaultLocale the default locale
     */
    public DefaultMessageCompositor(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    public void addDefaultPostProcessor(PostProcessor postProcessor)
    {
        this.defaultPostProcessors.add(postProcessor);
    }

    public final String composeMessage(String sourceMessage, Object... messageArguments)
    {
        return this.composeMessage(this.defaultLocale, sourceMessage, messageArguments);
    }

    public final String composeMessage(Locale locale, String sourceMessage, Object... messageArguments)
    {
        return new Message(this, locale, sourceMessage, messageArguments).process();
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
            List<Macro> list = this.macros.get(name);
            if (list == null)
            {
                list = new ArrayList<Macro>();
                this.macros.put(name, list);
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

    public final MessageCompositor registerReader(String key, Reader reader)
    {
        readers.put(key, reader);
        return this;
    }

    public final MessageCompositor registerReader(Class<? extends Macro> macroClass, String key, Reader reader)
    {
        Map<String, Reader> mReaders = mappedReaders.get(macroClass);
        if (mReaders == null)
        {
            mReaders = new HashMap<String, Reader>();
            mappedReaders.put(macroClass, mReaders);
        }
        mReaders.put(key, reader);
        return this;
    }

    public final MessageCompositor registerDefaultReader(Class<? extends Macro> macroClass, Reader reader)
    {
        this.defaultReaders.put(macroClass, reader);
        return this;
    }

    @SuppressWarnings("unchecked")
    public final <T> T read(String key, String value, Class<T> clazz)
    {
        Reader reader = this.readers.get(key);
        if (reader == null)
        {
            return null;
        }
        return (T)reader.read(value);
    }

    @SuppressWarnings("unchecked")
    public final <T> T read(Macro macro, String key, String value, Class<T> clazz)
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
        return (T)reader.read(value);
    }

    @SuppressWarnings("unchecked")
    public final <T> T read(Macro macro, String value, Class<T> clazz)
    {
        Reader reader = this.defaultReaders.get(macro.getClass());
        if (reader == null)
        {
            return null;
        }
        return (T)reader.read(value);
    }

    @SuppressWarnings("unchecked")
    final Macro matchMacroFor(Object messageArgument, Collection<Macro> macroList)
    {
        ConstantMacro cMacro = null;
        for (Macro macro : macroList)
        {
            if (macro instanceof ConstantMacro)
            {
                cMacro = (ConstantMacro)macro;
            }
            else if (messageArgument != null && macro instanceof Formatter && ((Formatter)macro).isApplicable(
                messageArgument.getClass()))
            {
                return macro;
            }
        }
        return cMacro;
    }


    final Macro matchMacroFor(Object messageArgument, String type)
    {
        List<Macro> list = this.macros.get(type);
        if (list == null)
        {
            return null;
        }
        return this.matchMacroFor(messageArgument, list);
    }

    @SuppressWarnings("unchecked")
    final String format(MacroContext context, Object arg)
    {
        Macro macro = context.getMacro();
        String processed;
        if (macro instanceof Formatter)
        {
            processed = ((Formatter)macro).process(arg, context);
        }
        else if (macro instanceof ConstantMacro)
        {
            processed = ((ConstantMacro)macro).process(context);
        }
        else
        {
            throw new IllegalArgumentException("Unknown Macro! " + macro.getClass().getName());
        }
        Collection<PostProcessor> postProcessors = macro.getPostProcessors();
        if (postProcessors.isEmpty())
        {
            postProcessors = this.defaultPostProcessors;
        }
        for (PostProcessor postProcessor : postProcessors)
        {
            processed = postProcessor.process(processed, context);
        }
        return processed;
    }
}
