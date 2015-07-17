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
package org.cubeengine.dirigent;

import java.util.Locale;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.PostProcessor;

/**
 * The main interface of this API.
 * Provides methods to compose a Message
 *
 * @param <MessageT> the resulting MessageType
 */
public interface Dirigent<MessageT>
{
    /**
     * Composes a message using the {@link Locale#getDefault()} Locale.
     * @param source the source message
     * @param args the message arguments
     * @return the composed Message
     */
    MessageT compose(String source, Object... args);

    /**
     * Composes a message
     * @param locale the locale
     * @param source the source message
     * @param args the message arguments
     * @return the composed message
     */
    MessageT compose(Locale locale, String source, Object... args);

    /**
     * Adds a new Formatter to use when composing the messages
     * @param formatter the formatter to add
     * @return fluent interface
     */
    Dirigent registerFormatter(Formatter<?> formatter);

    /**
     * Adds a new PostProcessor to run over all MessageComponents
     * @param postProcessor the PostProcessor to add
     * @return fluent interface
     */
    Dirigent addPostProcessor(PostProcessor postProcessor);

    /**
     * Finds a Formatter for given name and argument
     * @param name the name of the formatter
     * @param arg the argument to pass to the formatter
     * @return the formatter or null if not found
     */
    Formatter findFormatter(String name, Object arg);
}
