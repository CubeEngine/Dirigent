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
package org.cubeengine.dirigent.formatter.reflected;

import java.lang.reflect.Method;

/**
 * This exception is thrown if the {@link Format} annotation is on a method which can't be used for formatting.
 */
public class InvalidFormatMethodException extends RuntimeException
{
    private final Class<?> clazz;
    private final Method method;

    public InvalidFormatMethodException(Class<?> clazz, Method method, String message)
    {
        super(makeMessage(clazz, method, message));
        this.clazz = clazz;
        this.method = method;
    }

    public InvalidFormatMethodException(Class<?> clazz, Method method, String message, Throwable cause)
    {
        super(makeMessage(clazz, method, message), cause);
        this.clazz = clazz;
        this.method = method;
    }

    public Class<?> getClazz()
    {
        return clazz;
    }

    public Method getMethod()
    {
        return method;
    }

    private static String makeMessage(Class<?> clazz, Method method, String message)
    {
        return clazz.getName() + "." + method.getName() + ": " + message;
    }
}
