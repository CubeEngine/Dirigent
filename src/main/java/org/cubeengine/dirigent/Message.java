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
package org.cubeengine.dirigent;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

/**
 * A parsed Message
 */
public class Message
{
    public static Message EMPTY = new Message(Collections.<Component>emptyList());

    private final List<Component> components;

    public Message(List<Component> components)
    {
        this.components = unmodifiableList(components);
    }

    public Message(Component component)
    {
        this.components = singletonList(component);
    }

    /**
     * Returns the components of the parsed message
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
        if (!(o instanceof Message))
        {
            return false;
        }

        final Message message = (Message)o;

        return getComponents().equals(message.getComponents());
    }

    @Override
    public int hashCode()
    {
        return getComponents().hashCode();
    }

    @Override
    public String toString()
    {
        return "Message{" + "components=" + components + '}';
    }
}
