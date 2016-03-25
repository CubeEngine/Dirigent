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
package org.cubeengine.dirigent.parser;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.dirigent.Message;
import org.cubeengine.dirigent.parser.component.ErrorComponent;
import org.cubeengine.dirigent.parser.component.macro.CompleteMacro;
import org.cubeengine.dirigent.parser.component.macro.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.component.macro.Macro;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.macro.NamedMacro;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Flag;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

import static java.lang.Integer.parseInt;
import static org.cubeengine.dirigent.parser.component.macro.DefaultMacro.DEFAULT_MACRO;

/**
 * Parses a raw message to a {@link Message} consisting of {@link Component}s
 */
public class Parser
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';
    public static final char MACRO_SEPARATOR = ':';
    public static final char MACRO_LABEL = '#';
    public static final char ARGUMENT_VALUE = '=';

    private Parser()
    {}

    public static Message parseMessage(String message)
    {
        return readMessage(new RawMessage(message));
    }

    private static Message readMessage(RawMessage message)
    {
        final List<Component> elements = new ArrayList<Component>();
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
                        elements.add(new IllegalMacro(message.fromCheckPoint(), e.getMessage()));
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
                return DEFAULT_MACRO;
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
        boolean commentEscaped = false;
        for (Character c : message) // read the name
        {
            if (!commentEscaped && (c == MACRO_SEPARATOR || c == MACRO_END)) // end of name
            {
                if (c == MACRO_END) // end of macro
                {
                    message.prev();
                }
                ended = true;
                break;
            }

            if (!commentEscaped && comment && c == MACRO_ESCAPE)
            {
                commentEscaped = true;
            }
            else if (!commentEscaped && c == MACRO_LABEL) // start of comment
            {
                comment = true;
            }
            else if (!comment) // if not comment add to name
            {
                sb.append(c);
                commentEscaped = false;
            }
            else if (commentEscaped)
            {
                commentEscaped = false;
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
        boolean escaped = false;
        for (Character c : message) // read argument-value
        {
            if (!escaped && (c == MACRO_SEPARATOR || c == MACRO_END)) // end of argument
            {
                message.prev();
                ended = true;
                break;
            }
            if (!escaped && c == MACRO_ESCAPE)
            {
                escaped = true;
            }
            else
            {
                sb.append(c);
                escaped = false;
            }
        }
        if (!ended)
        {
            throw new IllegalMacroException("argument-value");
        }
        return sb.toString();
    }

    /**
     * Show the original text of an invalid macro.
     * Gets added to the Message when an {@link IllegalMacroException} was thrown during parsing.
     */
    private static class IllegalMacro extends Text implements ErrorComponent
    {
        private String error;

        public IllegalMacro(String string, String error)
        {
            super(string);
            this.error = error;
        }

        @Override
        public String getError()
        {
            return error;
        }
    }
}
