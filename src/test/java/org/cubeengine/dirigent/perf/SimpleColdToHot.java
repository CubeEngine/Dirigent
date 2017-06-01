package org.cubeengine.dirigent.perf;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.parser.Tokenizer;
import org.cubeengine.dirigent.parser.token.CompleteMacro;
import org.cubeengine.dirigent.parser.token.DefaultMacro;
import org.cubeengine.dirigent.parser.token.Indexed;
import org.cubeengine.dirigent.parser.token.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.token.NamedMacro;
import org.cubeengine.dirigent.parser.token.Token;

import static java.util.Arrays.asList;

public class SimpleColdToHot
{

    public static void main(String[] args)
    {
        List<Class<?>> classes = asList(Tokenizer.class, Token.class, DefaultMacro.class, NamedMacro.class,
                                        IndexedDefaultMacro.class, Indexed.class, CompleteMacro.class, Argument.class,
                                        Value.class, Parameter.class, Pattern.class, Matcher.class);
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
            Tokenizer.tokenize(msg);
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
