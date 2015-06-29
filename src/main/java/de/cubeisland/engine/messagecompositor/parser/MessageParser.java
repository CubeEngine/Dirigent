package de.cubeisland.engine.messagecompositor.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import de.cubeisland.engine.messagecompositor.RawMessage;
import de.cubeisland.engine.messagecompositor.parser.ArgumentElement;
import de.cubeisland.engine.messagecompositor.parser.Element;
import de.cubeisland.engine.messagecompositor.parser.MacroElement;
import de.cubeisland.engine.messagecompositor.parser.Message;
import de.cubeisland.engine.messagecompositor.parser.StringElement;

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

    public static Message readMessage(String message)
    {
        return readMessage(new RawMessage(message));
    }

    private static Message readMessage(RawMessage message)
    {
        final LinkedList<Element> elements = new LinkedList<Element>();
        for (char c : message) // Read entire the raw message
        {
            switch (c)
            {
                case MACRO_BEGIN: // start macro
                    elements.add(readMacro(message));
                    break;
                default: // start normal text
                    elements.add(readString(message));
            }
        }

        return new Message(elements);
    }

    private static StringElement readString(RawMessage message)
    {
        StringBuilder sb = new StringBuilder().append(message.current());
        for (char c : message)
        {
            switch (c)
            {
                case MACRO_BEGIN: // end normal text
                    message.prev();
                    return new StringElement(sb.toString());
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
        return new StringElement(sb.toString());
    }

    private static MacroElement readMacro(RawMessage message)
    {
        boolean ended = false;
        Integer index = null;
        String name = null;
        List<ArgumentElement> arguments = null;
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
                arguments = readArguments(message);
            }
        }
        if (!ended)
        {
            throw new IllegalArgumentException(); // TODO message macro not closed / checkpoint?
        }
        return new MacroElement(index, name, arguments);
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
                throw new IllegalArgumentException(); // TODO invalid index
            }
        }
        if (!ended) // index did not end
        {
            throw new IllegalArgumentException(); // TODO index not closed
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
            throw new IllegalArgumentException(); // TODO name not closed
        }
        return sb.toString();
    }

    private static List<ArgumentElement> readArguments(RawMessage message)
    {
        boolean ended = false;
        List<ArgumentElement> list = new ArrayList<ArgumentElement>();
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
            throw new IllegalArgumentException(); // TODO arguments not closed
        }
        return list;
    }

    private static ArgumentElement readArgument(RawMessage message)
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
            throw new IllegalArgumentException(); // TODO argument not closed
        }
        if (argumentValue == null)
        {
            return new ArgumentElement(null, sb.toString());
        }
        return new ArgumentElement(sb.toString(), argumentValue);
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
            throw new IllegalArgumentException(); // TODO argument not closed
        }
        return sb.toString();
    }
}
