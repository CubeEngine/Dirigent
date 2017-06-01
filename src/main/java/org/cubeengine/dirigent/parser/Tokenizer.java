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

/**
 * Processes a raw message to a {@link TokenBuffer}
 */
public class Tokenizer
{
    private static final char MACRO_BEGIN = '{';
    private static final char MACRO_END = '}';
    private static final char LABEL_SEP = '#';
    private static final char SECTION_SEP = ':';
    private static final char VALUE_SEP = '=';
    static final char ESCAPE = '\\';

    public enum TokenType
    {
        PLAIN_STRING,
        ESCAPED_STRING,
        NUMBER,
        MACRO_BEGIN(Tokenizer.MACRO_BEGIN),
        MACRO_END(Tokenizer.MACRO_END),
        LABEL_SEPARATOR(Tokenizer.LABEL_SEP),
        SECTION_SEPARATOR(Tokenizer.SECTION_SEP),
        VALUE_SEPARATOR(Tokenizer.VALUE_SEP);

        public final char character;

        TokenType()
        {
            this('\0');
        }

        TokenType(char c)
        {
            this.character = c;
        }
    }

    public static final class TokenBuffer {
        public final int[] offsets;
        public final int[] lengths;
        public final TokenType[] types;
        public final int count;
        public final String data;

        public TokenBuffer(int[] offsets, int[] lengths, TokenType[] types, int count, String data)
        {
            this.offsets = offsets;
            this.lengths = lengths;
            this.types = types;
            this.count = count;
            this.data = data;
        }

        @Override
        public String toString()
        {
            StringBuilder s = new StringBuilder();
            s.append("TokenBuffer(");
            if (count > 0)
            {
                s.append('"');
                s.append(types[0].name());
                s.append('(');
                s.append(data, offsets[0], offsets[0] + lengths[0]);
                s.append(')');
                for (int i = 1; i < count; i++)
                {
                    s.append(", ");
                    s.append(types[i].name());
                    s.append('(');
                    s.append(data, offsets[i], offsets[i] + lengths[i]);
                    s.append(')');
                }
            }
            s.append(')');

            return s.toString();
        }
    }

    public static TokenBuffer tokenize(final String input)
    {

        // currently in an unclosed macro?
        boolean insideMacro = false;
        // seen a label in the currently open macro ?
        boolean hasLabel = false;
        // should the next character be interpreted as text no matter what?
        boolean textExpected = false;
        // seen a label separator
        boolean hasSections = false;

        int[] starts = new int[input.length()];
        int[] lengths = new int[input.length()];
        TokenType[] types = new TokenType[input.length()];
        int tokenCount = 0;

        int offset = 0;
        int prevOffset = -1;
        int lastBeginMacro = -1;
        char c;
        while (offset < input.length())
        {
            if (offset == prevOffset)
            {
                throw new IllegalStateException("Detected endless loop!");
            }
            prevOffset = offset;

            c = input.charAt(offset);
            switch (c)
            {
                case MACRO_BEGIN:
                    insideMacro = true;
                    hasLabel = false;
                    lastBeginMacro = tokenCount;
                    hasSections = false;

                    types[tokenCount] = TokenType.MACRO_BEGIN;
                    starts[tokenCount] = offset;
                    lengths[tokenCount] = 1;
                    tokenCount++;
                    offset++;
                    break;
                case MACRO_END:
                    insideMacro = false;

                    types[tokenCount] = TokenType.MACRO_END;
                    starts[tokenCount] = offset;
                    lengths[tokenCount] = 1;
                    tokenCount++;
                    offset++;
                    break;
                case LABEL_SEP:
                    hasLabel = true;
                    textExpected = true;

                    types[tokenCount] = TokenType.LABEL_SEPARATOR;
                    starts[tokenCount] = offset;
                    lengths[tokenCount] = 1;
                    tokenCount++;
                    offset++;
                    break;
                case SECTION_SEP:
                    textExpected = true;
                    hasSections = true;

                    types[tokenCount] = TokenType.SECTION_SEPARATOR;
                    starts[tokenCount] = offset;
                    lengths[tokenCount] = 1;
                    tokenCount++;
                    offset++;
                    break;
                case VALUE_SEP:
                    textExpected = true;

                    types[tokenCount] = TokenType.VALUE_SEPARATOR;
                    starts[tokenCount] = offset;
                    lengths[tokenCount] = 1;
                    tokenCount++;
                    offset++;
                    break;
                default:
                    textExpected = true;
            }
            if (textExpected)
            {
                textExpected = false;
                int start = offset;
                TokenType type = TokenType.PLAIN_STRING;

                // the current char can't be the end
                offset++;
                boolean hasEscaping;
                boolean isNumeric = insideMacro && !hasSections && isDigit(c);

                if (offset < input.length())
                {
                    hasEscaping = c == ESCAPE;
                    c = input.charAt(offset);
                    while (!stringEnd(c, insideMacro, hasEscaping, hasLabel))
                    {
                        isNumeric = isNumeric && isDigit(c);

                        if (hasEscaping && type == TokenType.PLAIN_STRING)
                        {
                            type = TokenType.ESCAPED_STRING;
                        }
                        hasEscaping = c == ESCAPE;
                        offset++;
                        if (offset >= input.length())
                        {
                            break;
                        }
                        c = input.charAt(offset);
                    }
                }
                int length = offset - start;
                if (length > 0)
                {
                    starts[tokenCount] = start;
                    lengths[tokenCount] = length;
                    types[tokenCount] = isInt(input, start, length, isNumeric) ? TokenType.NUMBER : type;
                    tokenCount++;
                }
            }
        }

        if (insideMacro)
        {
            TokenType replacementType = TokenType.PLAIN_STRING;
            for (int i = lastBeginMacro + 1; i < tokenCount; ++i)
            {
                if (types[i] == TokenType.ESCAPED_STRING)
                {
                    replacementType = TokenType.ESCAPED_STRING;
                    break;
                }
            }
            types[lastBeginMacro] = replacementType;
            lengths[lastBeginMacro] = input.length() - starts[lastBeginMacro];
            tokenCount = lastBeginMacro + 1;
            int beforeLastBeginMacro = lastBeginMacro - 1;
            if (beforeLastBeginMacro >= 0 && replacementType == types[beforeLastBeginMacro])
            {
                lengths[beforeLastBeginMacro] += lengths[lastBeginMacro];
                tokenCount--;
            }
        }

        return new TokenBuffer(starts, lengths, types, tokenCount, input);
    }

    private static boolean isInt(String input, int offset, int length, boolean numeric)
    {
        return numeric && (length == 1 || isNonZeroDigit(input.charAt(offset)));
    }

    private static boolean stringEnd(char c, boolean insideMacro, boolean escaped, boolean hasLabel)
    {
        if (insideMacro)
        {
            return !escaped && (
                c == SECTION_SEP ||
                c == MACRO_END ||
                c == VALUE_SEP ||
                (!hasLabel && c == LABEL_SEP)
            );
        }
        else
        {
            return !escaped && c == MACRO_BEGIN;
        }
    }

    /**
     * Checks if the given character is a non-zero decimal digit.
     *
     * @param c the character
     * @return true if it is a non-zero digit
     */
    private static boolean isNonZeroDigit(char c)
    {
        return c >= '1' && c <= '9';
    }

    /**
     * Checks if the given character is a deciaml digit.
     *
     * @param c the character
     * @return true if it is a decimal digit
     */
    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }
}
