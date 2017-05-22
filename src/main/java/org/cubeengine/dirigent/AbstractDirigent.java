/*
 * The MIT License
 * Copyright © 2013 Cube Island
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
package org.cubeengine.dirigent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.cubeengine.dirigent.parser.Parser;
import org.cubeengine.dirigent.parser.component.FoundFormatter;
import org.cubeengine.dirigent.parser.component.MissingFormatter;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.Indexed;
import org.cubeengine.dirigent.parser.component.macro.Macro;
import org.cubeengine.dirigent.parser.component.macro.NamedMacro;
import org.cubeengine.dirigent.formatter.ConstantFormatter;
import org.cubeengine.dirigent.formatter.Context;
import org.cubeengine.dirigent.formatter.DefaultFormatter;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.PostProcessor;

import static java.util.Collections.emptyList;

/**
 * Basic implementation of Dirigent providing:
 * - Parsing the message but not composing the final message
 * - Formatters and PostProcessors
 */
public abstract class AbstractDirigent<MessageT> implements Dirigent<MessageT>
{
    private Map<String, List<Formatter>> formatters = new HashMap<String, List<Formatter>>();
    private List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();

    public AbstractDirigent()
    {
        registerFormatter(new DefaultFormatter());
    }

    @Override
    public MessageT compose(String source, Object... args)
    {
        return this.compose(Locale.getDefault(), source, args);
    }

    @Override
    public MessageT compose(Locale locale, String source, Object... args)
    {
        Message message = check(locale, Parser.parseMessage(source), args);
        return compose(message);
    }

    /**
     * Composes the parsed Message into the final form
     * @param message the parsed message
     * @return the composed Message
     */
    protected abstract MessageT compose(Message message);

    @Override
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

    @Override
    public Dirigent addPostProcessor(PostProcessor postProcessor)
    {
        postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public Dirigent registerFormatter(Formatter<?> formatter)
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

    /**
     * Finds Formatter for the messages components and runs the global PostProcessors.
     *
     * @param locale the locale
     * @param message the parsed message
     * @param args the message arguments
     * @return the modified Message ready to be composed
     */
    private Message check(Locale locale, Message message, Object[] args)
    {
        List<Component> list = new ArrayList<Component>();
        Context context = new Context(locale);
        int argumentsIndex = 0;
        for (Component component : message.getComponents())
        {
            if (component instanceof Text)
            {
                list.add(component);
            }
            else if (component instanceof Macro)
            {
                int forIndex = argumentsIndex;
                if (component instanceof Indexed)
                {
                    forIndex = ((Indexed)component).getIndex();
                }
                String name = null; // DefaultMacro
                List<Argument> arguments = emptyList();
                if (component instanceof NamedMacro)
                {
                    name = ((NamedMacro)component).getName();
                    arguments = ((NamedMacro)component).getArgs();
                }
                Object arg = null; // may be null because it might be a constant macro
                if (forIndex < args.length)
                {
                    arg = args[forIndex];
                }
                Formatter found = this.findFormatter(name, arg);
                if (found == null)
                {
                    list.add(new MissingFormatter(((Macro)component), arg));
                }
                if (found instanceof ConstantFormatter)
                {
                    arg = null;
                }

                if (found != null)
                {
                    list.add(new FoundFormatter(found, arg, arguments, context));
                }

                if (forIndex == argumentsIndex)
                {
                    if (!(found instanceof ConstantFormatter))
                    {
                        argumentsIndex++;
                    }
                }

            }
            else
            {
                throw new IllegalStateException("The message contains Components that are not Text or Macro: " + component.getClass().getName());
            }
        }

        if (!postProcessors.isEmpty())
        {
            for (int i = 0; i < list.size(); i++)
            {
                Component component = list.get(i);
                for (PostProcessor processor : postProcessors)
                {
                    component = processor.process(component, context.with(Collections.<Argument>emptyList()));
                }
                list.set(i, component);
            }
        }
        if (list.equals(message.getComponents()))
        {
            return message;
        }

        return new Message(list);
    }
}
