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
package org.cubeengine.dirigent.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.argument.Arguments;

/**
 * Formatters are used to format message parameters. In general a specific formatter can be used for a specific object
 * type which is the generic type of this class.
 *
 * A {@link PostProcessor} can be added to a Formatter to run after the formatting process and manipulate the created
 * {@link Component}.
 *
 * @param <T> the object type to format
 */
public abstract class Formatter<T>
{
    /**
     * The attached {@link PostProcessor}s.
     */
    private List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();

    /**
     * Returns true if given parameter can be formatted with this Formatter
     *
     * @param input The input value to format.
     *
     * @return whether the parameter can be formatted
     */
    public abstract boolean isApplicable(Object input);

    /**
     * Formats the parameter into a {@link Component} for given compose {@link Context} with the help of the specified
     * {@link Arguments} object.
     *
     * @param input The message input parameter to format.
     * @param context The compose context.
     * @param args The arguments of the macro.
     *
     * @return the resulting Component
     */
    protected abstract Component format(T input, Context context, Arguments args);

    /**
     * Formats the parameter into a {@link Component} for given compose {@link Context} with the help of the specified
     * {@link Arguments} object. Therefore it calls the {@link #format(Object, Context, Arguments)} method which must
     * be implemented by sub classes. Afterwards all attached {@link PostProcessor}s are executed.
     *
     * @param input the message input parameter to format.
     * @param context the compose context.
     * @param args The arguments of the macro.
     *
     * @return the resulting processed Component
     */
    public final Component process(T input, Context context, Arguments args)
    {
        Component result = format(input, context, args);
        for (PostProcessor processor : postProcessors)
        {
            result = processor.process(result, context, args);
        }
        return result;
    }

    /**
     * Adds a PostProcessor to this Formatter
     *
     * @param pp the PostProcessor to add
     */
    public final void addPostProcessor(PostProcessor pp)
    {
        postProcessors.add(pp);
    }

    /**
     * Returns the names of this formatter
     *
     * @return the names
     */
    public abstract Set<String> names();
}
