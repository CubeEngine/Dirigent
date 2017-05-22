/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.Message;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.CompleteMacro;
import org.cubeengine.dirigent.parser.component.macro.IllegalMacro;
import org.cubeengine.dirigent.parser.component.macro.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.component.macro.NamedMacro;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Flag;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;

import static java.lang.Integer.parseInt;
import static org.cubeengine.dirigent.parser.component.macro.DefaultMacro.DEFAULT_MACRO;

/**
 * Parses a raw message to a {@link Message} consisting of {@link Component}s
 */
public class Parser
{
    // DON'T LOOK AT THIS!                                          |  some text          |      |  the index        | |the type|  |the label| |  the parameters                      |  some more text            |
    private static final Pattern TEXT_AND_MACRO = Pattern.compile("^((?:\\\\[{\\\\]|[^{])*)(?:\\{(?:(?:(0|[1-9]\\d*):)?([^:#}]+)(?:#([^:}]+))?(:[^=:}]+(?:=(?:\\\\[:}\\\\]|[^:}])+)?)*)?}|(\\{(?:\\\\[{\\\\]|[^{])*))?");
    private static final Pattern INTEGER = Pattern.compile("^(?:0|[1-9]\\d*)$");
    private static final Pattern ARGUMENT = Pattern.compile("^:([^=:]+)(?:=((?:\\\\[:}\\\\]|[^:}])+))?");

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
        //String label;
        String params;
        String suffix;
        while (matcher.find())
        {
            prefix = matcher.group(1);
            if (prefix.length() > 0)
            {
                components.add(new Text(stripBackslashes(prefix, "\\{")));
            }

            index = matcher.group(2);
            name = matcher.group(3);
            boolean noIndex = index == null;
            boolean noName = name == null;
            if (noIndex && noName)
            {
                components.add(DEFAULT_MACRO);
            }
            else
            {
                //label = matcher.group(4);
                params = matcher.group(5);

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

            suffix = matcher.group(6);
            if (suffix != null)
            {
                components.add(new IllegalMacro(stripBackslashes(suffix, "\\{"), "Encountered macro start, but no valid macro followed."));
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
            name = matcher.group(1);
            value = matcher.group(2);
            if (value == null)
            {
                args.add(new Flag(name));
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
                if (c != '\\' || charset.indexOf(input.charAt(i + 1)) == -1) {
                    stripped.append(c);
                }
            }
            return stripped.append(i).toString();
        }
    }

}
