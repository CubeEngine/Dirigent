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
package org.cubeengine.dirigent.parser;

import org.cubeengine.dirigent.parser.component.TextComponent;
import org.cubeengine.dirigent.parser.element.Element;

/**
 * A simple component and token for static strings.
 */
public class Text implements Element, TextComponent
{
    public static final Text EMPTY = new Text("");

    /**
     * The actual text.
     */
    private String string;

    /**
     * Constructor.
     *
     * @param string The actual text.
     */
    public Text(String string)
    {
        this.string = String.valueOf(string);
    }

    /**
     * Returns the actual text.
     *
     * @return the text.
     */
    public String getText()
    {
        return string;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Text))
        {
            return false;
        }

        final Text text = (Text)o;

        return getText().equals(text.getText());
    }

    @Override
    public int hashCode()
    {
        return getText().hashCode();
    }

    @Override
    public String toString()
    {
        return "Text{" + "string='" + string + '\'' + '}';
    }

    public static Text create(String s)
    {
        if (s.isEmpty())
        {
            return EMPTY;
        }
        return new Text(s);
    }

    public static Text append(Text a, Text b)
    {
        return create(a.getText() + b.getText());
    }
}
