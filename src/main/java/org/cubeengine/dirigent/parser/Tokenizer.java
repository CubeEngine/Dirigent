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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cubeengine.dirigent.parser.token.CompleteMacro;
import org.cubeengine.dirigent.parser.token.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.token.NamedMacro;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.parser.token.Token;

import static java.util.regex.Pattern.compile;
import static org.cubeengine.dirigent.parser.token.DefaultMacro.DEFAULT_MACRO;

/**
 * Processes a raw message to a {@link List} consisting of {@link Token}'s
 */
public class Tokenizer
{
    private static final Pattern TEXT_AND_MACRO = compile(
        //  |  some text          |             | index     |   | name  || label                    | |  the parameters                                        |         | broken macro            |
        "\\G((?:\\\\[{\\\\]|[^{])*)(?:(\\{(?:(?:(0|[1-9]\\d*):)?([^:#}]+)(?:#(?:\\\\[:}\\\\]|[^:}])+)?((?::(?:\\\\[=:}\\\\]|[^=:}])+(?:=(?:\\\\[:}\\\\]|[^:}])+)?)+)?)?})|(\\{(?:\\\\[{\\\\]|[^{])*))?");
    private static final Pattern ARGUMENT = compile("\\G:((?:\\\\[=:}\\\\]|[^=:}])+)(?:=((?:\\\\[:}\\\\]|[^:}])+))?");

    private static final int GROUP_TEXT_PREFIX = 1;
    private static final int GROUP_WHOLE_MACRO = 2;
    private static final int GROUP_INDEX = 3;
    private static final int GROUP_NAME = 4;
    private static final int GROUP_PARAMS = 5;
    private static final int GROUP_BROKEN_MACRO = 6;

    private static final int GROUP_PARAM_NAME = 1;
    private static final int GROUP_PARAM_VALUE = 2;

    /**
     * Takes a raw input messages and splits it into its lexical parts.
     *
     * @param message the raw input message
     * @return a list of lexical tokens
     */
    public static List<Token> tokenize(final String message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("message may not be null!");
        }
        if (message.isEmpty())
        {
            return Collections.emptyList();
        }
        if (message.indexOf('{') == -1)
        {
            return Collections.<Token>singletonList(new Text(message));
        }

        Matcher textMatcher = TEXT_AND_MACRO.matcher(message);
        Matcher argMatcher = ARGUMENT.matcher("");

        List<Token> components = new ArrayList<Token>();
        String prefix;
        String index;
        String name;
        String brokenMacroRest;
        while (textMatcher.find())
        {
            prefix = textMatcher.group(GROUP_TEXT_PREFIX);
            if (prefix.length() > 0)
            {
                components.add(new Text(unescape(prefix, "\\{")));
            }

            if (textMatcher.start(GROUP_WHOLE_MACRO) != -1)
            {

                index = textMatcher.group(GROUP_INDEX);
                name = textMatcher.group(GROUP_NAME);
                boolean noIndex = index == null;
                boolean noName = name == null;
                if (noIndex && noName)
                {
                    components.add(DEFAULT_MACRO);
                }
                else
                {
                    if (noIndex)
                    {
                        if (textMatcher.start(GROUP_PARAMS) == -1 && isInt(name))
                        {
                            components.add(new IndexedDefaultMacro(toInt(name)));
                        }
                        else
                        {
                            components.add(new NamedMacro(name, parseArguments(textMatcher.group(GROUP_PARAMS), argMatcher)));
                        }
                    }
                    else
                    {
                        components.add(new CompleteMacro(toInt(index), name, parseArguments(textMatcher.group(GROUP_PARAMS), argMatcher)));
                    }
                }

            }

            brokenMacroRest = textMatcher.group(GROUP_BROKEN_MACRO);
            if (brokenMacroRest != null)
            {
                components.add(new InvalidMacro(unescape(brokenMacroRest, "\\{")));
            }
            if (textMatcher.hitEnd())
            {
                break;
            }
        }

        return components;
    }

    /**
     * Converts the given string into an integer using Horner's method.
     * No validation is done on the input. This method will produce numbers, even if the input is not a valid decimal
     * number.
     *
     * @param s an input string consisting of decimal digits
     * @return the integer representation of the input string if possible
     */
    private static int toInt(String s)
    {
        int len = s.length();
        if (len == 1)
        {
            return s.charAt(0) - '0';
        }

        int out = 0;
        for (int i = len - 1, factor = 1; i >= 0; --i, factor *= 10)
        {
            out += (s.charAt(i) - '0') * factor;
        }
        return out;
    }

    /**
     * Checks the given string is a valid unsigned integer.
     *
     * @param s the input
     * @return true if the input is a valid unsigned integer.
     */
    private static boolean isInt(String s)
    {
        int len = s.length();
        if (len == 0)
        {
            return false;
        }
        if (len == 1)
        {
            return isDigit(s.charAt(0));
        }
        else
        {
            if (!isNonZeroDigit(s.charAt(0)))
            {
                return false;
            }
            for (int i = 1; i < len; ++i)
            {
                if (!isDigit(s.charAt(i)))
                {
                    return false;
                }
            }
            return true;
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

    /**
     * Decomposes the given arguments string to the separate arguments using the given {@link Matcher}.
     *
     * @param params the arguments string
     * @param matcher the {@link Matcher} with the pattern to use
     * @return the list og arguments
     */
    private static List<Argument> parseArguments(String params, Matcher matcher)
    {
        if (params == null || params.isEmpty())
        {
            return Collections.emptyList();
        }
        List<Argument> args = new ArrayList<Argument>(1);

        matcher.reset(params);
        String name;
        String value;
        while (matcher.find())
        {
            name = unescape(matcher.group(GROUP_PARAM_NAME), "=:}\\");
            value = matcher.group(GROUP_PARAM_VALUE);
            if (value == null)
            {
                args.add(new Value(name));
            }
            else
            {
                args.add(new Parameter(name, unescape(value, ":}")));
            }
        }

        return args;
    }

    /**
     * Strips escaping backslashes from the given input string.
     *
     * @param input the input string with escaping backslashes
     * @param charset the set of characters that need escaping
     * @return the unescaped string
     */
    static String unescape(String input, String charset)
    {
        if (input.indexOf('\\') == -1 || input.length() <= 1)
        {
            return input;
        }
        else
        {
            StringBuilder stripped = new StringBuilder();
            char c, n;
            int i;
            for (i = 0; i < input.length() - 1; ++i)
            {
                c = input.charAt(i);
                if (c == '\\')
                {
                    n = input.charAt(i + 1);
                    if (charset.indexOf(n) == -1)
                    {
                        stripped.append(c);
                    }
                    else
                    {
                        stripped.append(n);
                        ++i;
                    }
                }
                else
                {
                    stripped.append(c);
                }
            }
            if (i < input.length())
            {
                stripped.append(input.charAt(i));
            }
            return stripped.toString();
        }
    }
}
