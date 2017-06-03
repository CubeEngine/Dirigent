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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cubeengine.dirigent.context.Arguments;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.element.CompleteMacro;
import org.cubeengine.dirigent.parser.element.Element;
import org.cubeengine.dirigent.parser.element.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.element.NamedMacro;

public class TestHelper
{
    public static Arguments toArgs(Object... args)
    {
        List<String> values = new ArrayList<String>();
        Map<String, String> params = new HashMap<String, String>();
        for (final Object arg : args)
        {
            if (arg instanceof String)
            {
                values.add((String)arg);
            }
            else
            {
                String[] pair = (String[])arg;
                params.put(pair[0].toLowerCase(), pair[1]);
            }
        }
        return Arguments.create(values, params);
    }

    public static List<Element> elems(Element... components)
    {
        return Arrays.asList(components);
    }

    public static Element txt(String s)
    {
        return new Text(s);
    }

    public static Element named(String name, Object... args)
    {
        return new NamedMacro(name, toArgs(args));
    }

    public static Element indexed(int i)
    {
        return new IndexedDefaultMacro(i);
    }

    public static Element complete(int i, String name, Object... args)
    {
        return new CompleteMacro(i, name, toArgs(args));
    }

    public static Object arg(String name)
    {
        return name;
    }

    public static Object arg(String name, String val)
    {
        return new String[]{name, val};
    }
}
