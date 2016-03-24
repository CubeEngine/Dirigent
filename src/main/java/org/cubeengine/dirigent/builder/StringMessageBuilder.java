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
package org.cubeengine.dirigent.builder;

import org.cubeengine.dirigent.parser.component.ErrorComponent;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;

/**
 * Builds a String using a StringBuilder
 */
public class StringMessageBuilder extends MessageBuilder<String, StringBuilder>
{
    @Override
    public void build(Text component, StringBuilder builder)
    {
        builder.append(component.getString());
    }

    @Override
    public StringBuilder newBuilder()
    {
        return new StringBuilder();
    }

    @Override
    public String finalize(StringBuilder stringBuilder)
    {
        return stringBuilder.toString();
    }

    @Override
    public void build(ErrorComponent component, StringBuilder builder)
    {
        if (component instanceof Text)
        {
            builder.append(((Text)component).getString());
        }
        builder.append(component.getError());
    }

    @Override
    public void buildOther(Component component, StringBuilder builder)
    {
        throw new IllegalStateException("Custom components not supported"); // No custom Components
    }
}
