/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
