/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import org.junit.Test;

public class MessageParserTest
{
    @Test
    public void testReadMessage() throws Exception
    {
        MessageParser.parseMessage("only text");
        MessageParser.parseMessage("{}");
        MessageParser.parseMessage("{name}");
        MessageParser.parseMessage("{0}");
        MessageParser.parseMessage("{1:name#with index and comment}");
        MessageParser.parseMessage("{1:name#with index and comment:and parameter}");
        MessageParser.parseMessage("{1:name#with index and comment:and parameter=with value}");
        MessageParser.parseMessage("{1:name#with index and comment:and parameter=with value:multiple}");
        MessageParser.parseMessage("{1:name#with index and comment:and parameter=with value:multiple:and one=more}");
        MessageParser.parseMessage(
            "text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text");
        MessageParser.parseMessage(
            "text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text");

        MessageParser.parseMessage("illegal macro {starts but wont end");
        MessageParser.parseMessage("illegal macro {starts:has arguments but wont end");
    }
}