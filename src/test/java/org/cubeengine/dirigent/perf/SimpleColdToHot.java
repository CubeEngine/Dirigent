package org.cubeengine.dirigent.perf;

import org.cubeengine.dirigent.parser.Tokenizer;

public class SimpleColdToHot
{

    public static void main(String[] args)
    {
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
