package org.cubeengine.dirigent.perf;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.parser.Parser;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.parser.component.TextComponent;
import org.cubeengine.dirigent.parser.element.CompleteMacro;
import org.cubeengine.dirigent.parser.element.DefaultMacro;
import org.cubeengine.dirigent.parser.element.Element;
import org.cubeengine.dirigent.parser.element.Indexed;
import org.cubeengine.dirigent.parser.element.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.element.Macro;
import org.cubeengine.dirigent.parser.element.NamedMacro;

import static java.util.Arrays.asList;

public class SimpleColdToHot
{

    public static void main(String[] args)
    {
        List<Class<?>> classes = asList(Parser.class, Macro.class, Element.class, DefaultMacro.class, NamedMacro.class,
                                        IndexedDefaultMacro.class, Indexed.class, CompleteMacro.class, Argument.class,
                                        Value.class, Parameter.class, Pattern.class, Matcher.class, Text.class,
                                        TextComponent.class, Component.class);
        System.out.println("Classes:");
        System.out.println(classes);
        String msg = "text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text";
        long start, delta;
        long values = 0;
        double avg = 0;
        int printAt = 1;

        for (int i = 0; i < 100000000; i++)
        {
            start = System.nanoTime();
            Parser.parse(msg);
            delta = System.nanoTime() - start;
            avg = (avg * values + delta) / (values + 1);
            values++;
            if (i == printAt - 1 || i < 10)
            {
                System.out.println("Time at " + (i + 1) + ": " + (avg / 1000d) + "µs");
                values = 0;
                avg = 0;
                if (i == printAt - 1) printAt *= 10;
            }
        }
    }
}
