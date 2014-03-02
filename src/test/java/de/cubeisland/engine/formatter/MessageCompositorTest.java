package de.cubeisland.engine.formatter;

import java.util.Date;

import de.cubeisland.engine.formatter.formatter.example.DateFormatter;
import junit.framework.TestCase;

public class MessageCompositorTest extends TestCase
{
    private MessageCompositor compositor;

    @Override
    protected void setUp() throws Exception
    {
        this.compositor = new MessageCompositor();
        this.compositor.registerFormatter(new DateFormatter());
    }

    public void testComposeMessage()
    {
        System.out.println(compositor.composeMessage("Year: {date:format=YYYY}", new Date()));
        System.out.println(compositor.composeMessage("Date is: {date:format=YYYY-mm-DD}", new Date()));
        System.out.println(compositor.composeMessage("Without Arguments; {date}", new Date()));
    }
}
