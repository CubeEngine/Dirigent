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
import org.cubeengine.dirigent.parser.component.ComponentGroup;
import org.cubeengine.dirigent.parser.component.ResolvedMacro;
import org.cubeengine.dirigent.parser.Text;
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
     *
     * @return the built message
     */
    public abstract MessageT finalize(BuilderT builderT);

    /**
     * Appends a Component to the builder
     *
     * @param component the component
     * @param builder   the builder
     */
    public final void buildAny(Component component, BuilderT builder)
    {
        if (component instanceof ResolvedMacro)
        {
            buildFormatted((ResolvedMacro)component, builder);
        }
        else if (component instanceof UnresolvableMacro)
        {
            build((UnresolvableMacro)component, builder);
        }
        else if (component instanceof TextComponent)
        {
            build((TextComponent)component, builder);
        }
        else if (component instanceof ComponentGroup)
        {
            buildGroup((ComponentGroup)component, builder);
        }
        else
        {
            buildOther(component, builder);
        }
    }

    /**
     * Appends a {@link Text} Component to the builder
     *
     * @param component the text
     * @param builder   the builder
     */
    public abstract void build(TextComponent component, BuilderT builder);

    /**
     * Appends a {@link ResolvedMacro} Component to the builder
     *
     * @param c       the found formatter
     * @param builder the builder
     */
    public final void buildFormatted(ResolvedMacro c, BuilderT builder)
    {
        @SuppressWarnings("unchecked") Component processed = c.getFound().process(c.getInput(), c.getContext(),
                                                                                  c.getArguments());
        buildAny(processed, builder);
    }

    /**
     * Handles a {@link UnresolvableMacro}
     *
     * @param component the component
     * @param builder   the builder
     */
    public abstract void build(UnresolvableMacro component, BuilderT builder);

    /**
     * Appends a chain of Components to the builder
     *
     * @param chained the chained Components
     * @param builder the builder
     */
    public void buildGroup(ComponentGroup chained, BuilderT builder)
    {
        for (Component component : chained.getComponents())
        {
            buildAny(component, builder);
        }
    }

    /**
     * Appends a Component to the builder.
     * Implement this to handle custom Components
     *
     * @param component the component
     * @param builder   the builder
     */
    public abstract void buildOther(Component component, BuilderT builder);
}
