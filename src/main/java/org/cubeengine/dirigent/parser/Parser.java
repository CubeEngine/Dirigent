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
package org.cubeengine.dirigent.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cubeengine.dirigent.context.Arguments;
import org.cubeengine.dirigent.parser.element.CompleteMacro;
import org.cubeengine.dirigent.parser.element.DefaultMacro;
import org.cubeengine.dirigent.parser.element.Element;
import org.cubeengine.dirigent.parser.element.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.element.NamedMacro;

import static java.util.Collections.emptyList;
import static org.cubeengine.dirigent.parser.ParserHelper.inArray;

/**
 * Grammar:
 *
 * message   := parts
 * parts     := part+
 * part      := text | macro
 * macro     := '{' body? '}'
 * body      := indexed | named
 * indexed   := index (':' named)?
 * named     := name label? arguments
 * label     := '#' string
 * arguments := (':' argument)*
 * argument  := (name '=')? value
 * text      := string
 * value     := string
 * name      := string
 * index     := NUMBER
 * string    := PLAIN_STRING | ESCAPED_STRING | NUMBER
 */
public class Parser
{
    private static final char MACRO_BEGIN = '{';
    private static final char MACRO_END = '}';
    private static final char LABEL_SEP = '#';
    private static final char SECTION_SEP = ':';
    private static final char VALUE_SEP = '=';
    private static final char ESCAPE = '\\';

    // these sets are in ascending char order as per int code
    private static final char[] TEXT_FOLLOW = {MACRO_BEGIN};
    private static final char[] SECTION_FOLLOW = {SECTION_SEP, MACRO_END};
    private static final char[] INDEX_FOLLOW = SECTION_FOLLOW;
    private static final char[] MACRO_NAME_FOLLOW = {LABEL_SEP, SECTION_SEP, MACRO_END};
    private static final char[] LABEL_FOLLOW = SECTION_FOLLOW;
    private static final char[] PARAM_NAME_FOLLOW = {SECTION_SEP, VALUE_SEP, MACRO_END};

    private static final class State
    {
        private final String in;
        private final List<Element> out;
        private int offset = 0;

        public State(String in, List<Element> out)
        {
            this.in = in;
            this.out = out;
        }

        boolean outOfInput()
        {
            return offset >= in.length();
        }

        void output(Element e)
        {
            out.add(e);
        }

        @Override
        public String toString()
        {
            String elems = "Elements: " + out;
            if (outOfInput())
            {
                return elems;
            }
            return "State: offset=" + this.offset + ", char=" + this.in.charAt(offset) + ", " + elems;
        }
    }

    public static List<Element> parse(String message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("message may not be null!");
        }
        if (message.isEmpty())
        {
            return emptyList();
        }
        State s = new State(message, new ArrayList<Element>(1));
        parseParts(s);

        return ParserHelper.mergeAdjacentTexts(s.out);
    }

    private static void parseParts(State s)
    {
        while (!s.outOfInput())
        {
            parsePart(s);
        }
    }

    private static void parsePart(State s)
    {
        if (is(s, MACRO_BEGIN))
        {
            parseMacro(s);
        }
        else
        {
            parseText(s, false);
        }
    }

    private static void parseText(State s, boolean forceFirst)
    {
        s.output(Text.create(readUntil(s, TEXT_FOLLOW, forceFirst)));
    }

    private static String readUntil(State s, char[] endChars, boolean forceFirst)
    {
        final int length = s.in.length();
        if (s.offset >= length)
        {
            return "";
        }
        int start = s.offset;
        if (forceFirst)
        {
            ++s.offset;
        }
        StringBuilder builder = null;
        char current, next;
        while (s.offset < length)
        {
            current = s.in.charAt(s.offset);
            if (inArray(endChars, current))
            {
                break;
            }
            if (current == ESCAPE && s.offset + 1 < length)
            {
                next = s.in.charAt(s.offset + 1);
                if (next == ESCAPE || inArray(endChars, next))
                {
                    if (builder == null)
                    {
                        builder = new StringBuilder((s.offset - start) + 1);
                    }
                    builder.append(s.in, start, s.offset).append(next);
                    // skip ESCAPE and next
                    s.offset += 2;
                    start = s.offset;
                }
                else
                {
                    ++s.offset;
                }
            }
            else
            {
                ++s.offset;
            }
        }
        if (builder == null)
        {
            return s.in.substring(start, s.offset);
        }
        else
        {
            if (start != s.offset)
            {
                builder.append(s.in, start, s.offset);
            }
            return builder.toString();
        }
    }

    private static void parseMacro(State s)
    {
        // skip MACRO_BEGIN
        int start = s.offset++;
        if (s.outOfInput())
        {
            s.offset = start;
            parseText(s, true);
        }
        else
        {
            if (is(s, MACRO_END))
            {
                s.output(DefaultMacro.DEFAULT_MACRO);
                ++s.offset;
            }
            else if (ParserHelper.isDigit(s.in.charAt(s.offset)))
            {
                parseIndexedMacro(s, start);
            }
            else
            {
                parseNamedMacro(s, start);
            }
        }
    }

    private static void parseIndexedMacro(State s, int start)
    {
        int index = readIndex(s);
        if (index == -1)
        {
            s.offset = start + 1;
            parseNamedMacro(s, start);
        }
        else
        {
            if (is(s, MACRO_END))
            {
                s.out.add(new IndexedDefaultMacro(index));
                // skip MACRO_END
                ++s.offset;
            }
            else
            {
                // skip SECTION_SEP
                ++s.offset;
                parseNamedMacroWithIndex(s, start, index);
            }
        }
    }

    private static int readIndex(State s)
    {
        int start = s.offset;
        char current;
        boolean numeric = true;
        while (s.offset < s.in.length())
        {
            current = s.in.charAt(s.offset);
            if (inArray(INDEX_FOLLOW, current))
            {
                break;
            }
            ++s.offset;
            numeric = numeric && ParserHelper.isDigit(current);
        }
        int len = s.offset - start;
        if (!numeric)
        {
            return -1;
        }
        if (len > 1 && !ParserHelper.isNonZeroDigit(s.in.charAt(start)))
        {
            return -1;
        }
        return ParserHelper.toInt(s.in, start, len);
    }

    private static void parseNamedMacro(State s, int start)
    {
        parseNamedMacroWithIndex(s, start, -1);
    }

    private static void parseNamedMacroWithIndex(State s, int start, int index)
    {
        String name = readUntil(s, MACRO_NAME_FOLLOW, true);
        if (is(s, LABEL_SEP))
        {
            // skip LABEL_SEP
            ++s.offset;
            readUntil(s, LABEL_FOLLOW, false);
        }
        if (s.outOfInput())
        {
            // backtrack
            s.offset = start;
            parseText(s, true);
            return;
        }
        final Arguments args;
        if (is(s, SECTION_SEP))
        {
            args = parseArguments(s);
            if (args == null)
            {
                // parsing arguments failed, backtrack
                s.offset = start;
                parseText(s, true);
                return;
            }
        }
        else
        {
            args = Arguments.NONE;
        }
        if (is(s, MACRO_END))
        {
            if (index == -1)
            {
                s.output(new NamedMacro(name, args));
            }
            else
            {
                s.output(new CompleteMacro(index, name, args));
            }
            // skip MACRO_END
            ++s.offset;
        }
        else
        {
            s.offset = start;
            parseText(s, true);
        }
    }

    private static Arguments parseArguments(State s)
    {
        List<String> values = null;
        Map<String, String> params = null;
        while (is(s, SECTION_SEP))
        {
            // skip SECTION_SEP
            ++s.offset;

            final String name = readUntil(s, PARAM_NAME_FOLLOW, false);
            if (is(s, VALUE_SEP))
            {
                // skip VALUE_SEP
                ++s.offset;
                if (name.isEmpty())
                {
                    return null;
                }
                else
                {
                    if (params == null)
                    {
                        params = new HashMap<String, String>(1);
                    }
                    params.put(name.toLowerCase(), readUntil(s, SECTION_FOLLOW, false));
                }
            }
            else
            {
                if (values == null)
                {
                    values = new ArrayList<String>(1);
                }
                values.add(name);
            }
        }
        return Arguments.create(values, params);
    }

    private static boolean is(State s, char c)
    {
        return s.offset < s.in.length() && s.in.charAt(s.offset) == c;
    }

    /**
     * Escapes the given string so that it is safe to be used as a positional argument value.
     *
     * @param s the string to be escaped
     * @return the escaped string
     */
    public static String escapeArgumentValue(String s) {
        return s.replace("" + SECTION_SEP, "" + ESCAPE + SECTION_SEP)
                .replace("" + VALUE_SEP, "" + ESCAPE + VALUE_SEP)
                .replace("" + MACRO_END, "" + ESCAPE + MACRO_END);
    }

    /**
     * Escapes the given string so that it is safe to be used as a named parameter name.
     *
     * @param s the string to be escaped
     * @return the escaped string
     */
    public static String escapeParameterName(String s) {
        return s.replace("" + SECTION_SEP, "" + ESCAPE + SECTION_SEP)
                .replace("" + VALUE_SEP, "" + ESCAPE + VALUE_SEP)
                .replace("" + MACRO_END, "" + ESCAPE + MACRO_END);
    }

    /**
     * Escapes the given string so that it is safe to be used as a named parameter value.
     *
     * @param s the string to be escaped
     * @return the escaped string
     */
    public static String escapeParameterValue(String s) {
        return s.replace("" + SECTION_SEP, "" + ESCAPE + SECTION_SEP)
                .replace("" + MACRO_END, "" + ESCAPE + MACRO_END);
    }
}
