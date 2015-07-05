package de.cubeisland.engine.messagecompositor.parser.formatter;

import de.cubeisland.engine.messagecompositor.parser.component.MessageComponent;
import de.cubeisland.engine.messagecompositor.parser.component.Text;

public abstract class MessageBuilder<MessageT, BuilderT>
{
    public abstract BuilderT newBuilder();
    public abstract MessageT finalize(BuilderT builderT);

    public abstract void build(Text component, BuilderT builder);
    public abstract void buildOther(MessageComponent component, BuilderT builder);
}
