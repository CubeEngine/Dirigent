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

public class Arguments
{
    public static final Arguments NONE = new Arguments(Collections.<Argument>emptyList());

    private final List<String> values;
    private final Map<String, String> parameters;

    public Arguments(List<Argument> rawArgs)
    {
        List<String> values = new ArrayList<String>();
        Map<String, String> parameters = new HashMap<String, String>();
        for (final Argument arg : rawArgs)
        {
            if (arg instanceof Value)
            {
                values.add(((Value)arg).get());
            }
            else if (arg instanceof Parameter)
            {
                Parameter param = (Parameter)arg;
                parameters.put(param.getName().toLowerCase(), param.getValue());
            }
            else
            {
                throw new IllegalArgumentException("Unknown Argument instance: " + arg.getClass());
            }
        }
        this.values = Collections.unmodifiableList(values);
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    /**
     * Returns the list of arguments
     *
     * @return the list of arguments
     */
    public List<String> getValues()
    {
        return values;
    }

    /**
     * Returns the argument value for given name or null if not found.
     *
     * @param name the name
     *
     * @return the value of the Argument by name
     */
    public String get(String name)
    {
        return parameters.get(name.toLowerCase());
    }

    /**
     * Returns the argument value for given name or the given default if not found.
     *
     * @param name the name
     * @param def the default value
     *
     * @return the value of the Argument by name
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
     * Returns the unnamed value at a position or null if the position is not within bounds.
     *
     * @param i the position
     *
     * @return the Argument value
     */
    public String get(int i)
    {
        if (i >= 0 && i < values.size())
        {
            return values.get(i);
        }
        return null;
    }

    /**
     * Returns the unnamed value at a position or the default if the position is not within bounds.
     *
     * @param i the position
     * @param def the default value
     *
     * @return the value of the Argument by name
     */
    public String getOrElse(int i, String def)
    {
        String val = get(i);
        if (val == null)
        {
            return def;
        }
        return val;
    }

    /**
     * Returns whether the list of arguments contains given flag
     *
     * @param value the flag to check for
     *
     * @return whether the list of arguments contains given flag
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
        return "Arguments{" + "values=" + values + ", parameters=" + parameters + '}';
    }

    public static Arguments create()
    {
        return NONE;
    }

    public static Arguments create(Argument arg)
    {
        return create(Collections.singletonList(arg));
    }

    public static Arguments create(List<Argument> args)
    {
        if (args == null || args.isEmpty())
        {
            return NONE;
        }
        return new Arguments(args);
    }
}
