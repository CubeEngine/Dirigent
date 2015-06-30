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
package de.cubeisland.engine.messagecompositor.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import de.cubeisland.engine.messagecompositor.parser.formatter.DefaultFormatter;
import de.cubeisland.engine.messagecompositor.parser.formatter.Formatter;
import de.cubeisland.engine.messagecompositor.parser.formatter.PostProcessor;

import static java.util.Collections.unmodifiableList;

public abstract class AbstractMessageCompositor<MessageT> implements MessageCompositor<MessageT>
{
    private Map<String, List<Formatter>> formatters = new HashMap<String, List<Formatter>>();
    private List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();

    public AbstractMessageCompositor()
    {
        registerFormatter(new DefaultFormatter());
    }

    public MessageT composeMessage(String source, Object... args)
    {
        return this.composeMessage(Locale.getDefault(), source, args);
    }

    public MessageT composeMessage(Locale locale, String source, Object... args)
    {
        Message message = MessageParser.parseMessage(source).check(this, args, unmodifiableList(postProcessors));
        return composeMessage(message);
    }

    protected abstract MessageT composeMessage(Message message);

    public Formatter findFormatter(String name, Object arg)
    {
        List<Formatter> list = this.formatters.get(name);
        if (list == null)
        {
            return null;
        }
        for (Formatter formatter : list)
        {
            if (formatter.isApplicable(arg))
            {
                return formatter;
            }
        }
        return null;
    }

    public MessageCompositor addPostProcessor(PostProcessor postProcessor)
    {
        postProcessors.add(postProcessor);
        return this;
    }

    public MessageCompositor registerFormatter(Formatter<?> formatter)
    {
        for (String name : formatter.names())
        {
            List<Formatter> list = this.formatters.get(name);
            if (list == null)
            {
                list = new ArrayList<Formatter>();
                formatters.put(name, list);
            }
            list.add(formatter);
        }
        return this;
    }
}
