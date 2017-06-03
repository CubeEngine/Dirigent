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
