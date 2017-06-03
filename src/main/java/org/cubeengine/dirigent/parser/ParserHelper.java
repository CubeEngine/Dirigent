package org.cubeengine.dirigent.parser;

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
}
