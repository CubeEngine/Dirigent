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
package org.cubeengine.dirigent.parser.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Multiple components grouped together to form a single logical unit.
 * This can be used to implement nesting.
 */
public class ComponentGroup implements Component
{
    public static final ComponentGroup EMPTY = new ComponentGroup(Collections.<Component>emptyList());

    private List<Component> components;

    public ComponentGroup(List<Component> components)
    {
        this.components = Collections.unmodifiableList(components);
    }

    public ComponentGroup(Component... components)
    {
        this(Arrays.asList(components));
    }

    /**
     * Returns the components of this group.
     *
     * @return the components
     */
    public List<Component> getComponents()
    {
        return components;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ComponentGroup))
        {
            return false;
        }

        final ComponentGroup that = (ComponentGroup)o;

        return getComponents().equals(that.getComponents());
    }

    @Override
    public int hashCode()
    {
        return getComponents().hashCode();
    }

    @Override
    public String toString()
    {
        return "ComponentGroup{" + "components=" + components + '}';
    }
}
