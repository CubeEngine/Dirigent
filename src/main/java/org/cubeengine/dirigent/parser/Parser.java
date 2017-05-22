/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.Message;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.CompleteMacro;
import org.cubeengine.dirigent.parser.component.macro.IllegalMacro;
import org.cubeengine.dirigent.parser.component.macro.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.component.macro.NamedMacro;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Value;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static org.cubeengine.dirigent.parser.component.macro.DefaultMacro.DEFAULT_MACRO;

/**
 * Parses a raw message to a {@link Message} consisting of {@link Component}s
 */
public class Parser
{
    // DON'T LOOK AT THIS!                                    |  some text          |            | index     |   | name  || label   | |  the parameters                      |         | broken macro            |
    private static final Pattern TEXT_AND_MACRO = compile(
        "\\G((?:\\\\[{\\\\]|[^{])*)(?:(\\{(?:(?:(0|[1-9]\\d*):)?([^:#}]+)(?:#(?:\\\\[:}\\\\]|[^:}])+)?((?::(?:\\\\[=:}\\\\]|[^=:}])+(?:=(?:\\\\[:}\\\\]|[^:}])+)?)+)?)?})|(\\{(?:\\\\[{\\\\]|[^{])*))?");
    private static final Pattern INTEGER = compile("(?:0|[1-9]\\d*)");
    private static final Pattern ARGUMENT = compile("\\G:((?:\\\\[=:}\\\\]|[^=:}])+)(?:=((?:\\\\[:}\\\\]|[^:}])+))?");

    private static final int GROUP_TEXT_PREFIX = 1;
    private static final int GROUP_WHOLE_MACRO = 2;
    private static final int GROUP_INDEX = 3;
    private static final int GROUP_NAME = 4;
    private static final int GROUP_PARAMS = 5;
    private static final int GROUP_BROKEN_MACRO = 6;

    private static final int GROUP_PARAM_NAME = 1;
    private static final int GROUP_PARAM_VALUE = 2;

    private Parser()
    {
    }

    public static Message parseMessage(final String message)
    {
        if (message.indexOf('{') == -1)
        {
            return new Message(new Text(message));
        }

        Matcher matcher = TEXT_AND_MACRO.matcher(message);

        List<Component> components = new ArrayList<Component>();
        String prefix;
        String index;
        String name;
        String params;
        String brokenMacroRest;
        while (matcher.find())
        {
            prefix = matcher.group(GROUP_TEXT_PREFIX);
            if (prefix.length() > 0)
            {
                components.add(new Text(stripBackslashes(prefix, "\\{")));
            }

            if (matcher.group(GROUP_WHOLE_MACRO) != null)
            {

                index = matcher.group(GROUP_INDEX);
                name = matcher.group(GROUP_NAME);
                boolean noIndex = index == null;
                boolean noName = name == null;
                if (noIndex && noName)
                {
                    components.add(DEFAULT_MACRO);
                }
                else
                {
                    params = matcher.group(GROUP_PARAMS);

                    if (noIndex)
                    {
                        if (params == null && INTEGER.matcher(name).matches())
                        {
                            components.add(new IndexedDefaultMacro(parseInt(name)));
                        }
                        else
                        {
                            components.add(new NamedMacro(name, parseArguments(params)));
                        }
                    }
                    else
                    {
                        components.add(new CompleteMacro(parseInt(index), name, parseArguments(params)));
                    }
                }

            }

            brokenMacroRest = matcher.group(GROUP_BROKEN_MACRO);
            if (brokenMacroRest != null)
            {
                components.add(new IllegalMacro(stripBackslashes(brokenMacroRest, "\\{"),
                                                "Encountered macro start, but no valid macro followed."));
            }
        }

        return new Message(components);
    }

    private static List<Argument> parseArguments(String params)
    {
        if (params == null || params.isEmpty())
        {
            return Collections.emptyList();
        }
        List<Argument> args = new ArrayList<Argument>();

        Matcher matcher = ARGUMENT.matcher(params);
        String name;
        String value;
        while (matcher.find())
        {
            name = stripBackslashes(matcher.group(GROUP_PARAM_NAME), "=:}\\");
            value = matcher.group(GROUP_PARAM_VALUE);
            if (value == null)
            {
                args.add(new Value(name));
            }
            else
            {
                args.add(new Parameter(name, stripBackslashes(value, ":}")));
            }
        }

        return args;
    }

    private static String stripBackslashes(String input, String charset)
    {
        if (input.length() <= 1)
        {
            return input;
        }
        else if (input.indexOf('\\') == -1)
        {
            return input;
        }
        else
        {
            StringBuilder stripped = new StringBuilder();
            char c;
            int i;
            for (i = 0; i < input.length() - 1; ++i)
            {
                c = input.charAt(i);
                if (c != '\\' || charset.indexOf(input.charAt(i + 1)) == -1)
                {
                    stripped.append(c);
                }
            }
            return stripped.append(input.charAt(i)).toString();
        }
    }
}
