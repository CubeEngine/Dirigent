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

import java.util.Arrays;
import java.util.Iterator;

public class RawMessage implements Iterator<Character>, Iterable<Character>
{
    private final char[] message;
    private int i = -1;
    private int checkpoint = 0;

    public RawMessage(String message)
    {
        this.message = message.toCharArray();
    }

    @Override
    public Iterator<Character> iterator()
    {
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return message.length > i + 1;
    }

    @Override
    public Character next()
    {
        return message[++i];
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public void prev()
    {
        i--;
    }

    public Character current()
    {
        return message[i];
    }

    public String fromCheckPoint()
    {
        return new String(Arrays.copyOfRange(message, checkpoint, message.length));
    }

    public void setCheckPoint()
    {
        checkpoint = i;
    }
}
