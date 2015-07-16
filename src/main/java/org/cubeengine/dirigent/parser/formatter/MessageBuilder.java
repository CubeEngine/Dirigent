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
package org.cubeengine.dirigent.parser.formatter;

import org.cubeengine.dirigent.parser.component.ChainedComponent;
import org.cubeengine.dirigent.parser.component.ErrorComponent;
import org.cubeengine.dirigent.parser.component.FoundFormatter;
import org.cubeengine.dirigent.parser.component.MessageComponent;
import org.cubeengine.dirigent.parser.component.Text;

public abstract class MessageBuilder<MessageT, BuilderT>
{
    public abstract BuilderT newBuilder();
    public abstract MessageT finalize(BuilderT builderT);

    public abstract void build(Text component, BuilderT builder);

    public void buildAny(MessageComponent component, BuilderT builder)
    {
        if (component instanceof FoundFormatter)
        {
            buildFormatted(((FoundFormatter)component), builder);
        }
        else if (component instanceof ChainedComponent)
        {
            buildChain(((ChainedComponent)component), builder);
        }
        else if (component instanceof Text)
        {
            build(((Text)component), builder);
        }
        else if (component instanceof ErrorComponent)
        {
            build(((ErrorComponent)component), builder);
        }
        else
        {
            buildOther(component, builder);
        }
        // TODO
    }

    public final void buildFormatted(FoundFormatter component, BuilderT builder)
    {
        @SuppressWarnings("unchecked")
        MessageComponent processed = component.getFound().process(component.getArg(), component.getContext());
        buildAny(processed, builder);
    }

    public abstract void build(ErrorComponent component, BuilderT builder);

    public void buildChain(ChainedComponent chained, BuilderT builder)
    {
        for (MessageComponent component : chained.getChained())
        {
            buildAny(component, builder);
        }
    }

    public abstract void buildOther(MessageComponent component, BuilderT builder);
}
