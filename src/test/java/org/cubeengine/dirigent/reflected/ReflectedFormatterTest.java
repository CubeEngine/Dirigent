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
package org.cubeengine.dirigent.reflected;

import org.cubeengine.dirigent.formatter.reflected.AnnotationMissingException;
import org.cubeengine.dirigent.formatter.reflected.Format;
import org.cubeengine.dirigent.formatter.reflected.InvalidFormatMethodException;
import org.cubeengine.dirigent.formatter.reflected.Names;
import org.cubeengine.dirigent.formatter.reflected.ReflectedFormatter;
import org.cubeengine.dirigent.parser.component.Component;
import org.junit.Test;

public class ReflectedFormatterTest
{
    @Test(expected = AnnotationMissingException.class)
    public void testWithoutFormatMethods()
    {
        @Names("test")
        class EmptyFormatter extends ReflectedFormatter
        {}
        new EmptyFormatter();
    }

    @Test(expected = AnnotationMissingException.class)
    public void testWithoutNames()
    {
        class UnnamedFormatter extends ReflectedFormatter
        {
            @Format public Component format(String s) {return null;}
        }
        new UnnamedFormatter();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testInvalidReturnType()
    {
        @Names("test")
        class InvalidReturn extends ReflectedFormatter {
            @Format
            public void test(String s) {};
        }

        new InvalidReturn();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testNoInput()
    {
        @Names("test")
        class NoInputs extends ReflectedFormatter {
            @Format
            public void test() {};
        }

        new NoInputs();
    }

    @Test
    public void testInvokers()
    {

    }
}
