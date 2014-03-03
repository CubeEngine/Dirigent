package de.cubeisland.engine.formatter;

import java.util.Date;

import de.cubeisland.engine.formatter.formatter.example.DateFormatter;
import de.cubeisland.engine.formatter.formatter.example.DecimalFormatter;
import de.cubeisland.engine.formatter.formatter.example.IntegerFormatter;
import junit.framework.TestCase;

public class MessageCompositorTest extends TestCase
{
    private MessageCompositor compositor;

    @Override
    protected void setUp() throws Exception
    {
        this.compositor = new MessageCompositor();
        this.compositor.registerFormatter(new DateFormatter());
        this.compositor.registerFormatter(new IntegerFormatter());
        this.compositor.registerFormatter(new DecimalFormatter());
    }

    public void testComposeMessage()
    {
        System.out.println(compositor.composeMessage("Year: {date:format=YYYY}", new Date()));
        System.out.println(compositor.composeMessage("Date is: {date:format=YYYY-mm-DD}", new Date()));
        System.out.println(compositor.composeMessage("Without Arguments; {date}", new Date()));
        System.out.println(compositor.composeMessage("Numbers: {number} {2:number} {number}", 1, 3 ,2));
        System.out.println(compositor.composeMessage("Decimal: {decimal} {2:decimal:2} {decimal:5}", 4.321, 5.4321 ,9.87654321));
    }
}
