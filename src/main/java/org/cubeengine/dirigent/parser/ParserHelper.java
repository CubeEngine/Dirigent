package org.cubeengine.dirigent.parser;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.dirigent.parser.element.Element;

import static java.util.Collections.emptyList;

public class ParserHelper
{
    /**
     * Checks if the given character is a non-zero decimal digit.
     *
     * @param c the character
     * @return true if it is a non-zero digit
     */
    public static boolean isNonZeroDigit(char c)
    {
        return c >= '1' && c <= '9';
    }

    /**
     * Checks if the given character is a deciaml digit.
     *
     * @param c the character
     * @return true if it is a decimal digit
     */
    public static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    /**
     * Converts the given string into an integer using Horner's method.
     * No validation isOneOf done on the input. This method will produce numbers, even if the input isOneOf not a valid decimal
     * number.
     *
     * @param input an input string consisting of decimal digits
     * @param offset the base offset in the input
     * @param length the number of characters to interpret
     *
     * @return the integer representation of the input string if possible
     */
    public static int toInt(String input, int offset, int length)
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


    public static List<Element> shakeIt(List<Element> in)
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

    public static boolean inArray(char[] chars, char c)
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
