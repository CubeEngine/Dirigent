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
import java.util.Collections;
import java.util.List;

import de.cubeisland.engine.messagecompositor.parser.component.argument.Argument;
import de.cubeisland.engine.messagecompositor.parser.component.macro.Indexed;
import de.cubeisland.engine.messagecompositor.parser.component.macro.Macro;
import de.cubeisland.engine.messagecompositor.parser.formatter.Formatter;
import de.cubeisland.engine.messagecompositor.parser.component.MessageComponent;
import de.cubeisland.engine.messagecompositor.parser.component.MissingMacro;
import de.cubeisland.engine.messagecompositor.parser.component.macro.NamedMacro;
import de.cubeisland.engine.messagecompositor.parser.component.Text;
import de.cubeisland.engine.messagecompositor.parser.formatter.PostProcessor;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class Message
{
    private final List<MessageComponent> components;

    public Message(List<MessageComponent> components)
    {
        this.components = unmodifiableList(components);
    }

    public List<MessageComponent> getComponents()
    {
        return components;
    }

    public Message check(MessageCompositor compositor, Object[] args, List<PostProcessor> postProcessors)
    {
        List<MessageComponent> list = new ArrayList<MessageComponent>();
        int argumentsIndex = 0;
        for (MessageComponent component : components)
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
                Object arg;
                try
                {
                    arg = args[forIndex];
                }
                catch (ArrayIndexOutOfBoundsException ignored)
                {
                    arg = null; // Might be a constant macro
                }
                Formatter found = compositor.findFormatter(name, arg);
                if (found == null)
                {
                    list.add(new MissingMacro(((Macro)component), arg));
                }
                else
                {
                    @SuppressWarnings("unchecked")
                    MessageComponent processed = found.process(arg, arguments);
                    list.add(processed);
                }
                if (forIndex == argumentsIndex)
                {
                    argumentsIndex++;
                }
            }
            else
            {
                // TODO   list.add(new WTFIsThis(component));
            }
        }

        if (!postProcessors.isEmpty())
        {
            for (int i = 0; i < components.size(); i++)
            {
                MessageComponent component = components.get(i);
                for (PostProcessor processor : postProcessors)
                {
                    component = processor.process(component, Collections.<Argument>emptyList());
                }
                components.set(i, component);
            }
        }
        if (list.equals(this.components))
        {
            return this;
        }

        return new Message(list);
    }
}
