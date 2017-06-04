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

import org.cubeengine.dirigent.context.Arguments;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.parser.component.ComponentGroup;

/**
 * This post processor wraps the input component with simple static components.
 * It could be used to wrap a component with html to bold something for example.
 */
public class WrappingPostProcessor implements PostProcessor
{
    private final Component before;
    private final Component after;

    /**
     * Constructor.
     *
     * @param before The component to add before the input component.
     * @param after The component to add after the input component.
     */
    public WrappingPostProcessor(Component before, Component after)
    {
        this.before = before;
        this.after = after;
    }

    /**
     * Constructor. Wraps the input component with static {@link org.cubeengine.dirigent.parser.component.TextComponent}s.
     *
     * @param beforeText The text to add before the input component.
     * @param afterText The text to add after the input component.
     */
    public WrappingPostProcessor(String beforeText, String afterText)
    {
        this(Text.create(beforeText), Text.create(afterText));
    }

    @Override
    public Component process(Component component, Context context, Arguments arguments)
    {
        return new ComponentGroup(before, component, after);
    }
}
