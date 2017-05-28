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
package org.cubeengine.dirigent.builder;

import org.cubeengine.dirigent.AbstractDirigent;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.parser.component.ComponentGroup;

/**
 * A Dirigent implementation using Builders.
 *
 * @param <MessageT> the resulting MessageType
 * @param <BuilderT> the Builder Type
 */
public class BuilderDirigent<MessageT, BuilderT> extends AbstractDirigent<MessageT>
{
    /**
     * The builder to use for composing the target message.
     */
    private final MessageBuilder<MessageT, BuilderT> mBuilder;

    /**
     * Constructor.
     *
     * @param mBuilder The builder to use for composing a {@link ComponentGroup} to the target message.
     */
    public BuilderDirigent(MessageBuilder<MessageT, BuilderT> mBuilder)
    {
        this.mBuilder = mBuilder;
    }

    /**
     * Constructor.
     *
     * @param mBuilder The builder to use for composing a {@link ComponentGroup} to the target message.
     * @param defaultFormatter The default formatter to use.
     */
    public BuilderDirigent(MessageBuilder<MessageT, BuilderT> mBuilder, Formatter<Object> defaultFormatter)
    {
        super(defaultFormatter);
        this.mBuilder = mBuilder;
    }

    @Override
    protected MessageT compose(ComponentGroup componentGroup, Context context)
    {
        BuilderT builder = mBuilder.newBuilder();
        mBuilder.buildGroup(componentGroup, builder, context);
        return mBuilder.finalize(builder, context);
    }
}
