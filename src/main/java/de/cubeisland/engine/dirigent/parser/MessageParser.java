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
package de.cubeisland.engine.dirigent.parser;

import java.util.ArrayList;
import java.util.List;
import de.cubeisland.engine.dirigent.RawMessage;
import de.cubeisland.engine.dirigent.parser.component.macro.CompleteMacro;
import de.cubeisland.engine.dirigent.parser.component.macro.DefaultMacro;
import de.cubeisland.engine.dirigent.parser.component.macro.IndexedDefaultMacro;
import de.cubeisland.engine.dirigent.parser.component.ErrorText;
import de.cubeisland.engine.dirigent.parser.component.macro.Macro;
import de.cubeisland.engine.dirigent.parser.component.MessageComponent;
import de.cubeisland.engine.dirigent.parser.component.macro.NamedMacro;
import de.cubeisland.engine.dirigent.parser.component.Text;
import de.cubeisland.engine.dirigent.parser.component.argument.Argument;
import de.cubeisland.engine.dirigent.parser.component.argument.Flag;
import de.cubeisland.engine.dirigent.parser.component.argument.Parameter;

import static java.lang.Integer.parseInt;

public class MessageParser
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';
    public static final char MACRO_LABEL = '#';
    public static final char ARGUMENT_VALUE = '=';

    private MessageParser()
    {}

    public static Message parseMessage(String message)
    {
        return readMessage(new RawMessage(message));
    }

    private static Message readMessage(RawMessage message)
    {
        final List<MessageComponent> elements = new ArrayList<MessageComponent>();
        for (char c : message) // Read entire the raw message
        {
            switch (c)
            {
                case MACRO_BEGIN: // start macro
                    try
                    {

                        elements.add(readMacro(message));
                        message.setCheckPoint();
                    }
                    catch (IllegalMacroException e)
                    {
                        elements.add(new ErrorText(message.fromCheckPoint(), e.getMessage()));
                        return new Message(elements);
                    }
                    break;
                default: // start normal text
                    elements.add(readString(message));
                    message.setCheckPoint();
            }
        }

        return new Message(elements);
    }

    private static Text readString(RawMessage message)
    {
        StringBuilder sb = new StringBuilder().append(message.current());
        for (char c : message)
        {
            switch (c)
            {
                case MACRO_BEGIN: // end normal text
                    message.prev();
                    return new Text(sb.toString());
                case MACRO_ESCAPE: // escaped text
                    if (message.hasNext())
                    {
                        c = message.next();
                    }
                    sb.append(c);
                default: // more normal text
                    sb.append(c);
            }
        }
        return new Text(sb.toString());
    }

    private static Macro readMacro(RawMessage message)
    {
        boolean ended = false;
        Integer index = null;
        String name = null;
        List<Argument> args = null;
        for (char c : message) // read the macro
        {
            if (c == MACRO_END) // end macro
            {
                ended = true;
                break;
            }
            if (name == null && index == null && Character.isDigit(c)) // index start
            {
                index = readIndex(message);
            }
            else if (name == null) // name start
            {
                name = readName(message);
            }
            else // start arguments
            {
                args = readArguments(message);
            }
        }
        if (!ended)
        {
            throw new IllegalMacroException("macro");
        }
        if (name == null)
        {
            if (index == null)
            {
                return new DefaultMacro();
            }
            return new IndexedDefaultMacro(index);
        }
        if (index == null)
        {
            return new NamedMacro(name, args);
        }
        return new CompleteMacro(index, name, args);
    }

    private static int readIndex(RawMessage message)
    {
        StringBuilder sb = new StringBuilder().append(message.current());
        boolean ended = false;
        for (Character c : message) // read the index
        {
            if (Character.isDigit(c)) // more digits
            {
                sb.append(c);
            }
            else if (c == MACRO_SEPARATOR || c == MACRO_END) // end of index
            {
                if (c == MACRO_END) // end of macro
                {
                    message.prev();
                }
                ended = true;
                break;
            }
            else // NaN invalid index
            {
                throw new IllegalMacroException("index");
            }
        }
        if (!ended) // index did not end
        {
            throw new IllegalMacroException("index");
        }
        return parseInt(sb.toString());
    }

    private static String readName(RawMessage message)
    {
        StringBuilder sb = new StringBuilder().append(message.current());
        boolean ended = false;
        boolean comment = false;
        for (Character c : message) // read the name
        {
            if (c == MACRO_SEPARATOR || c == MACRO_END) // end of name
            {
                if (c == MACRO_END) // end of macro
                {
                    message.prev();
                }
                ended = true;
                break;
            }
            if (c == MACRO_LABEL) // start of comment
            {
                comment = true;
            }
            else if (!comment) // if not comment add to name
            {
                sb.append(c);
            }
        }
        if (!ended)
        {
            throw new IllegalMacroException("name");
        }
        return sb.toString();
    }

    private static List<Argument> readArguments(RawMessage message)
    {
        boolean ended = false;
        List<Argument> list = new ArrayList<Argument>();
        list.add(readArgument(message)); // read first argument
        for (Character c : message)
        {
            if (c == MACRO_END) // end of name
            {
                message.prev();
                ended = true;
                break;
            }
            list.add(readArgument(message)); // read argument
        }
        if (!ended)
        {
            throw new IllegalMacroException("arguments");
        }
        return list;
    }

    private static Argument readArgument(RawMessage message)
    {
        StringBuilder sb = new StringBuilder().append(message.current());
        String argumentValue = null;
        boolean ended = false;
        for (Character c : message) // read the argument
        {
            if (c == MACRO_SEPARATOR || c == MACRO_END) // end of argument
            {
                if (c == MACRO_END) // end of macro
                {
                    message.prev();
                }
                ended = true;
                break;
            }
            if (c == ARGUMENT_VALUE) // start of argument-value
            {
                argumentValue = readArgumentValue(message);
            }
            else
            {
                sb.append(c);
            }
        }
        if (!ended)
        {
            throw new IllegalMacroException("argument");
        }
        if (argumentValue == null)
        {
            return new Flag(sb.toString());
        }
        return new Parameter(sb.toString(), argumentValue);
    }

    private static String readArgumentValue(RawMessage message)
    {
        StringBuilder sb = new StringBuilder();
        boolean ended = false;
        for (Character c : message) // read argument-value
        {
            if (c == MACRO_SEPARATOR || c == MACRO_END) // end of argument
            {
                message.prev();
                ended = true;
                break;
            }
            sb.append(c);
        }
        if (!ended)
        {
            throw new IllegalMacroException("argument-value");
        }
        return sb.toString();
    }
}
