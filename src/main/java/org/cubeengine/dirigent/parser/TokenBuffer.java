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
package org.cubeengine.dirigent.parser;

public final class TokenBuffer
{
    public final int[] offsets;
    public final int[] lengths;
    public final TokenType[] types;
    public final int count;
    public final String data;

    public TokenBuffer(int[] offsets, int[] lengths, TokenType[] types, int count, String data)
    {
        this.offsets = offsets;
        this.lengths = lengths;
        this.types = types;
        this.count = count;
        this.data = data;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append("TokenBuffer(");
        if (count > 0)
        {
            s.append('"');
            s.append(types[0].name());
            s.append('(');
            s.append(data, offsets[0], offsets[0] + lengths[0]);
            s.append(')');
            for (int i = 1; i < count; i++)
            {
                s.append(", ");
                s.append(types[i].name());
                s.append('(');
                s.append(data, offsets[i], offsets[i] + lengths[i]);
                s.append(')');
            }
        }
        s.append(')');

        return s.toString();
    }
}
