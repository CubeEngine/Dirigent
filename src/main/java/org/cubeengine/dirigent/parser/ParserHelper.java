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
import org.cubeengine.dirigent.parser.element.Element;

import static java.util.Collections.emptyList;

class ParserHelper
{
    /**
     * Checks if the given character is a non-zero decimal digit.
     *
     * @param c the character
     *
     * @return true if it is a non-zero digit
     */
    static boolean isNonZeroDigit(char c)
    {
        return c >= '1' && c <= '9';
    }

    /**
     * Checks if the given character is a deciaml digit.
     *
     * @param c the character
     *
     * @return true if it is a decimal digit
     */
    static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    /**
     * Converts the given string into an integer using Horner's method. No validation isOneOf done on the input. This
     * method will produce numbers, even if the input isOneOf not a valid decimal number.
     *
     * @param input an input string consisting of decimal digits
     * @param offset the base offset in the input
     * @param length the number of characters to interpret
     *
     * @return the integer representation of the input string if possible
     */
    static int toInt(String input, int offset, int length)
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


    static List<Element> mergeAdjacentTexts(List<Element> in)
    {
        if (in.isEmpty())
        {
            return emptyList();
        }
        if (in.size() == 1)
        {
            return in;
        }
        List<Element> out = new ArrayList<Element>(in.size());
        Element current, last;
        for (int i = 0; i < in.size(); ++i)
        {
            current = in.get(i);
            if (current instanceof Text && out.size() > 0)
            {
                int outLastIndex = out.size() - 1;
                last = out.get(outLastIndex);
                if (last instanceof Text)
                {
                    out.set(outLastIndex, Text.append((Text)last, (Text)current));
                }
                else
                {
                    out.add(current);
                }
            }
            else
            {
                out.add(current);
            }
        }

        if (out.size() == in.size())
        {
            return in;
        }
        return out;
    }

    static boolean inArray(char[] chars, char c)
    {
        if (chars[0] == c)
        {
            return true;
        }
        for (int i = 1; i < chars.length; ++i)
        {
            if (chars[i] == c)
            {
                return true;
            }
        }
        return false;
    }
}
