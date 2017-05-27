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

import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.component.ComponentGroup;
import org.cubeengine.dirigent.parser.component.ResolvedMacro;
import org.cubeengine.dirigent.parser.component.TextComponent;
import org.cubeengine.dirigent.parser.component.UnresolvableMacro;

/**
 * Uses a Builder to construct a message
 *
 * @param <MessageT> the resulting MessageType
 * @param <BuilderT> the Builder Type
 */
public abstract class MessageBuilder<MessageT, BuilderT>
{
    /**
     * Constructs a new Builder
     *
     * @return the new Builder
     */
    public abstract BuilderT newBuilder();

    /**
     * Returns the built message
     *
     * @param builderT the builder
     * @param context  the context
     *
     * @return the built message
     */
    public abstract MessageT finalize(BuilderT builderT, Context context);

    /**
     * Appends a Component to the builder
     *
     * @param component the component
     * @param builder   the builder
     * @param context   the context
     */
    public final void buildAny(Component component, BuilderT builder, Context context)
    {
        if (component instanceof ResolvedMacro)
        {
            buildResolved((ResolvedMacro)component, builder, context);
        }
        else if (component instanceof UnresolvableMacro)
        {
            buildUnresolvable((UnresolvableMacro)component, builder, context);
        }
        else if (component instanceof TextComponent)
        {
            buildText((TextComponent)component, builder, context);
        }
        else if (component instanceof ComponentGroup)
        {
            buildGroup((ComponentGroup)component, builder, context);
        }
        else
        {
            buildOther(component, builder, context);
        }
    }

    /**
     * Appends a {@link Text} Component to the builder
     *
     * @param component the text
     * @param builder   the builder
     * @param context   the context
     */
    public abstract void buildText(TextComponent component, BuilderT builder, Context context);

    /**
     * Appends a {@link ResolvedMacro} Component to the builder
     *
     * @param c       the found formatter
     * @param builder the builder
     * @param context the context
     */
    public final void buildResolved(ResolvedMacro c, BuilderT builder, Context context)
    {
        Component processed = c.getFormatter().process(c.getInput(), c.getContext(), c.getArguments());
        buildAny(processed, builder, context);
    }

    /**
     * Handles a {@link UnresolvableMacro}
     *
     * @param component the component
     * @param builder   the builder
     * @param context   the context
     */
    public abstract void buildUnresolvable(UnresolvableMacro component, BuilderT builder, Context context);

    /**
     * Appends a chain of Components to the builder
     *
     * @param group   the chained Components
     * @param builder the builder
     * @param context the context
     */
    public void buildGroup(ComponentGroup group, BuilderT builder, Context context)
    {
        for (Component component : group.getComponents())
        {
            buildAny(component, builder, context);
        }
    }

    /**
     * Appends a Component to the builder.
     * Implement this to handle custom Components
     *
     * @param component the component
     * @param builder   the builder
     * @param context   the context
     */
    public abstract void buildOther(Component component, BuilderT builder, Context context);
}
