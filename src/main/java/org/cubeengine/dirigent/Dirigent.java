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
package org.cubeengine.dirigent;

import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.PostProcessor;
import org.cubeengine.dirigent.parser.MacroResolutionResult;

/**
 * The main interface of this API.
 * Provides methods to compose a Message
 *
 * @param <MessageT> the resulting MessageType
 */
public interface Dirigent<MessageT>
{
    /**
     * Composes a message using a default {@link Context}.
     *
     * @param source The source message.
     * @param inputs The message parameters.
     *
     * @return the composed message.
     */
    MessageT compose(String source, Object... inputs);

    /**
     * Composes a message.
     *
     * @param context The compose context.
     * @param source The source message.
     * @param inputs The message parameters.
     *
     * @return the composed message.
     */
    MessageT compose(Context context, String source, Object... inputs);

    /**
     * Adds a new {@link Formatter} to use when composing the messages.
     *
     * @param formatter The formatter to add.
     *
     * @return fluent interface
     */
    Dirigent registerFormatter(Formatter<?> formatter);

    /**
     * Adds a new {@link PostProcessor} to run over all MessageComponents.
     *
     * @param postProcessor the PostProcessor to add
     *
     * @return fluent interface
     */
    Dirigent addPostProcessor(PostProcessor postProcessor);

    /**
     * Finds a {@link Formatter} for given name and input parameter.
     *
     * @param name The name of the formatter.
     * @param input The input parameter to pass to the formatter.
     *
     * @return a {@link MacroResolutionResult} representing a {@link org.cubeengine.dirigent.parser.MacroResolutionState}
     * and the {@link Formatter} to use.
     */
    MacroResolutionResult findFormatter(String name, Object input);
}
