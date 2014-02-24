package de.cubeisland.engine.formatter.util;

import java.util.Map;

public class MacroProcessor
{
    public static final char MACRO_BEGIN = '{';
    public static final char MACRO_END = '}';
    public static final char MACRO_ESCAPE = '\\';

    public String process(String message, Map<String, String> args)
    {
        StringBuilder finalString = new StringBuilder();

        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length; ++i)
        {
            switch (chars[i])
            {
                case MACRO_ESCAPE:
                    if (i + 1 < chars.length)
                    {
                        finalString.append(chars[++i]);
                        break;
                    }
                case MACRO_BEGIN:
                    if (i + 2 < chars.length)
                    {
                        int newOffset = replaceVar(finalString, chars, i, args);
                        if (newOffset > i)
                        {
                            i = newOffset;
                            break;
                        }
                    }
                default:
                    finalString.append(chars[i]);
            }
        }

        return finalString.toString();
    }

    private int replaceVar(StringBuilder out, char[] in, int offset, Map<String, String> values)
    {
        int i = offset + 1;
        String name = "";
        boolean done = false;
        for (; i < in.length && !done; ++i)
        {
            switch (in[i])
            {
                case MACRO_END:
                    done = true;
                    --i;
                    break;
                default:
                    name += in[i];
            }
        }

        String value = values.get(name);
        if (value != null)
        {
            out.append(value);
        }
        else
        {
            return offset;
        }

        return i;
    }
}
