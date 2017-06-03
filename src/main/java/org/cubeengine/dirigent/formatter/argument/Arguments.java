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
package org.cubeengine.dirigent.formatter.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * A class holding all {@link Argument} objects of a single {@link org.cubeengine.dirigent.parser.element.Macro}.
 * Furthermore it provides a few static helper methods to create a new instance.
 */
public class Arguments
{
    /**
     * Static reference of an arguments object without an argument.
     */
    public static final Arguments NONE = new Arguments(Collections.<Argument>emptyList());

    /**
     * List of {@link Value} arguments.
     */
    private final List<String> values;
    /**
     * Map of {@link Parameter} arguments.
     */
    private final Map<String, String> parameters;

    /**
     * Constructor. Splits the arguments into a {@link Value} list and a map for {@link Parameter} arguments.
     *
     * @param rawArgs The list of arguments.
     */
    public Arguments(List<Argument> rawArgs)
    {
        List<String> values = null;
        Map<String, String> parameters = null;
        for (final Argument arg : rawArgs)
        {
            if (arg instanceof Value)
            {
                if (values == null)
                {
                    values = new ArrayList<String>(1);
                }
                values.add(((Value)arg).get());
            }
            else if (arg instanceof Parameter)
            {
                Parameter param = (Parameter)arg;
                if (parameters == null)
                {
                    parameters = new HashMap<String, String>(1);
                }
                parameters.put(param.getName().toLowerCase(), param.getValue());
            }
            else
            {
                throw new IllegalArgumentException("Unknown Argument instance: " + arg.getClass());
            }
        }
        this.values = values == null ? Collections.<String>emptyList() : unmodifiableList(values);
        this.parameters = parameters == null ? Collections.<String, String>emptyMap() : unmodifiableMap(parameters);
    }

    /**
     * Returns the list of {@link Value} arguments.
     *
     * @return the list of arguments
     */
    public List<String> getValues()
    {
        return values;
    }

    /**
     * Returns the {@link Parameter} argument value for given name or {@code null} if not found.
     *
     * @param name The name of the {@link Parameter}.
     *
     * @return the value of the argument by name.
     */
    public String get(String name)
    {
        return parameters.get(name.toLowerCase());
    }

    /**
     * Returns the {@link Parameter} argument value for given name or the given default if not found.
     *
     * @param name The name of the {@link Parameter}.
     * @param def The default value.
     *
     * @return the value of the argument by name or the default value.
     */
    public String getOrElse(String name, String def)
    {
        String val = get(name);
        if (val == null)
        {
            return def;
        }
        return val;
    }

    /**
     * Returns the unnamed {@link Value} argument at a position or {@code null] if the position is not within bounds.
     *
     * @param i The position.
     *
     * @return the value argument.
     */
    public String get(int i)
    {
        return getOrElse(i, null);
    }

    /**
     * Returns the unnamed {@link Value} argument at a position or the default if the position is not within bounds.
     *
     * @param i the position
     * @param def the default value
     *
     * @return the value of the Argument by name
     */
    public String getOrElse(int i, String def)
    {
        if (i >= 0 && i < values.size())
        {
            return values.get(i);
        }
        return def;
    }

    /**
     * Returns whether the list of {@link Value} arguments contains a given value. This might be used to check for
     * specific flags.
     *
     * @param value The value argument to check for.
     *
     * @return whether the list of {@link Value} arguments contains the given value.
     */
    public boolean has(String value)
    {
        if (values.isEmpty())
        {
            return false;
        }
        for (String v : values)
        {
            if (v.equals(value))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Arguments))
        {
            return false;
        }

        final Arguments arguments = (Arguments)o;

        if (!values.equals(arguments.values))
        {
            return false;
        }
        return parameters.equals(arguments.parameters);
    }

    @Override
    public int hashCode()
    {
        int result = values.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        if (this == NONE)
        {
            return "NoArguments";
        }
        return "Arguments{" + "values=" + values + ", parameters=" + parameters + '}';
    }

    /**
     * Creates a new object without any arguments.
     *
     * @return the created object.
     */
    public static Arguments create()
    {
        return NONE;
    }

    /**
     * Creates a new object with the specified argument.
     *
     * @param arg The argument.
     *
     * @return the created object.
     */
    public static Arguments create(Argument arg)
    {
        return create(Collections.singletonList(arg));
    }

    /**
     * Creates a new object with the list of arguments. It checks for the number of argument entries and creates an
     * empty arguments object if necessary.
     *
     * @param args The list of arguments.
     *
     * @return the created object.
     */
    public static Arguments create(List<Argument> args)
    {
        if (args == null || args.isEmpty())
        {
            return NONE;
        }
        return new Arguments(args);
    }
}
