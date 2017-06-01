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
package org.cubeengine.dirigent.builder;

import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.parser.component.TextComponent;
import org.cubeengine.dirigent.parser.component.UnresolvableMacro;
import org.cubeengine.dirigent.parser.element.Macro;
import org.cubeengine.dirigent.parser.element.NamedMacro;

/**
 * Builds a String using a {@link StringBuilder}
 */
public class StringMessageBuilder extends MessageBuilder<String, StringBuilder>
{
    @Override
    public void buildText(TextComponent component, StringBuilder builder, Context context)
    {
        builder.append(component.getText());
    }

    @Override
    public StringBuilder newBuilder()
    {
        return new StringBuilder();
    }

    @Override
    public String finalize(StringBuilder stringBuilder, Context context)
    {
        return stringBuilder.toString();
    }

    @Override
    public void buildUnresolvable(UnresolvableMacro component, StringBuilder builder, Context context)
    {
        Macro macro = component.getMacro();
        if (macro instanceof NamedMacro)
        {
            builder.append("{{unresolved: ").append(((NamedMacro)macro).getName()).append("}}");
        }
        else
        {
            builder.append("{{unresolved}}");
        }
    }

    @Override
    public void buildOther(Component component, StringBuilder builder, Context context)
    {
        throw new IllegalStateException("Custom components not supported"); // No custom Components
    }
}
