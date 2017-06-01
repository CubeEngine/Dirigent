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
import java.util.List;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.parser.element.CompleteMacro;
import org.cubeengine.dirigent.parser.element.DefaultMacro;
import org.cubeengine.dirigent.parser.element.Element;
import org.cubeengine.dirigent.parser.element.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.element.NamedMacro;

import static java.util.Collections.emptyList;

public class Parser
{
    public static List<Element> parse(String message) {
        if (message == null)
        {
            throw new IllegalArgumentException("message may not be null!");
        }
        if (message.isEmpty())
        {
            return emptyList();
        }
        TokenBuffer buf = Tokenizer.tokenize(message);
        System.out.println("Input: " + message);
        System.out.println(buf);
        List<Element> elements = new ArrayList<Element>();
        parseParts(buf, elements);

        if (elements.isEmpty())
        {
            return emptyList();
        }
        return elements;
    }

    private static void parseParts(TokenBuffer buf, List<Element> elements)
    {
        int offset = 0;
        int prevOffset = -1;
        while (offset < buf.count)
        {
            if (prevOffset == offset)
            {
                throw new IllegalStateException("Detected endless loop!");
            }
            prevOffset = offset;
            offset = parsePart(buf, offset, elements);
        }
    }

    private static int parsePart(TokenBuffer buf, int offset, List<Element> elements)
    {
        if (is(buf, offset, TokenType.MACRO_BEGIN))
        {
            return parseMacro(buf, offset, elements);
        }
        else
        {
            return parseText(buf, offset, elements);
        }
    }

    private static int parseText(TokenBuffer buf, int offset, List<Element> elements)
    {
        if (isString(buf, offset))
        {
            elements.add(new Text(makeString(buf, offset, false)));
            return offset + 1;
        }
        else
        {
            elements.add(Text.EMPTY);
            return offset;
        }
    }

    private static int parseMacro(TokenBuffer buf, int offset, List<Element> elements)
    {
        // first is MACRO_BEGIN
        offset++;

        switch (buf.types[offset])
        {
            case MACRO_END:
                elements.add(DefaultMacro.DEFAULT_MACRO);
                return offset + 1;
            case NUMBER:
                return parseIndexedMacro(buf, offset, elements);
            default:
                return parseNamedMacro(buf, offset, elements);
        }
    }

    private static int parseIndexedMacro(TokenBuffer buf, int offset, List<Element> elements)
    {
        if (is(buf, offset, TokenType.NUMBER))
        {
            int index = makeInt(buf, offset);
            offset++;
            if (is(buf, offset, TokenType.MACRO_END))
            {
                elements.add(new IndexedDefaultMacro(index));
                return offset + 1;
            }
            else
            {
                return parseNamedMacroWithIndex(buf, offset + 1, elements, index);
            }
        }
        return offset;
    }

    private static int parseNamedMacro(TokenBuffer buf, int offset, List<Element> elements)
    {
        return parseNamedMacroWithIndex(buf, offset, elements, -1);
    }

    private static int parseNamedMacroWithIndex(TokenBuffer buf, int offset, List<Element> elements, int index)
    {
        String name = "";
        if (isString(buf, offset))
        {
            name = makeString(buf, offset, true);
            offset++;
        }
        if (is(buf, offset, TokenType.LABEL_SEPARATOR))
        {
            offset++;
            if (isString(buf, offset))
            {
                offset++;
            }
        }
        List<Argument> args;
        if (is(buf, offset, TokenType.SECTION_SEPARATOR))
        {
            args = new ArrayList<Argument>();
            offset = parseArguments(buf, offset, args);
            if (args.isEmpty())
            {
                args = emptyList();
            }
        }
        else
        {
            args = emptyList();
        }
        if (index == -1)
        {
            elements.add(new NamedMacro(name, args));
        }
        else
        {
            elements.add(new CompleteMacro(index, name, args));
        }
        return offset + 1;
    }

    private static int parseArguments(TokenBuffer buf, int offset, List<Argument> args)
    {
        while (!is(buf, offset, TokenType.MACRO_END))
        {
            offset = parseArgument(buf, offset + 1, args);
        }
        return offset;
    }

    private static int parseArgument(TokenBuffer buf, int offset, List<Argument> args)
    {
        String name = makeString(buf, offset, true);
        offset++;
        if (is(buf, offset, TokenType.VALUE_SEPARATOR))
        {
            offset++;
            args.add(new Parameter(name, makeString(buf, offset, true)));
            return offset + 1;
        }
        else
        {
            args.add(new Value(name));
            return offset;
        }
    }

    private static boolean is(TokenBuffer buf, int offset, TokenType t)
    {
        return buf.types[offset] == t;
    }

    private static boolean isString(TokenBuffer buf, int offset)
    {
        return is(buf, offset, TokenType.ESCAPED_STRING) || is(buf, offset, TokenType.PLAIN_STRING);
    }

    private static String makeString(TokenBuffer buf, int offset, boolean insideMacro)
    {
        final int dataOffset = buf.offsets[offset];
        if (is(buf, offset, TokenType.ESCAPED_STRING))
        {
            return unescape(buf.data, dataOffset, buf.lengths[offset], insideMacro);
        }
        else
        {
            return buf.data.substring(dataOffset, dataOffset + buf.lengths[offset]);
        }
    }

    private static int makeInt(TokenBuffer buf, int offset)
    {
        return toInt(buf.data, buf.offsets[offset], buf.lengths[offset]);
    }

    /**
     * Strips escaping backslashes from the given input string.
     *
     * @param input the input string with escaping backslashes
     * @param offset base offset in the input
     * @param length the number of characters to interpret
     * @param insideMacro whether the sequence is inside a macro
     * @return the unescaped string
     */
    static String unescape(String input, int offset, int length, boolean insideMacro)
    {
        if (length == 0)
        {
            return "";
        }
        if (length == 1)
        {
            return "" + input.charAt(offset);
        }
        // TODO decide if valid: if outside macro, but starts with {, unescape as if inside macro
        // this is based on the idea of invalid macros and should be reconsidered.
        // TODO this prevents the tokenizer from merging adjcacent strings when backtracking
        if (!insideMacro && input.charAt(offset) == Tokenizer.MACRO_BEGIN)
        {
            insideMacro = true;
        }
        StringBuilder stripped = new StringBuilder();
        int end = offset + length;
        char c, n;
        for (int i = offset; i < end; ++i)
        {
            c = input.charAt(i);
            if (c == Tokenizer.ESCAPE && i + 1 < end)
            {
                n = input.charAt(++i);
                if (Tokenizer.needsEscaping(n, insideMacro, true) || n == Tokenizer.ESCAPE)
                {
                    stripped.append(n);
                }
                else
                {
                    stripped.append(c).append(n);
                }
            }
            else
            {
                stripped.append(c);
            }
        }
        return stripped.toString();
    }


    /**
     * Converts the given string into an integer using Horner's method.
     * No validation is done on the input. This method will produce numbers, even if the input is not a valid decimal
     * number.
     *
     * @param input an input string consisting of decimal digits
     * @param offset the base offset in the input
     * @param length the number of characters to interpret
     * @return the integer representation of the input string if possible
     */
    private static int toInt(String input, int offset, int length)
    {
        if (length == 1)
        {
            return input.charAt(offset) - '0';
        }

        int out = 0;
        for (int i = offset + length - 1, factor = 1; i >= offset; --i, factor *= 10)
        {
            out += (input.charAt(i) - '0') * factor;
        }
        return out;
    }
}
