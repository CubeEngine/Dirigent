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
