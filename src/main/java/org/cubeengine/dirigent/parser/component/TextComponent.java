package org.cubeengine.dirigent.parser.component;

import org.cubeengine.dirigent.Component;

/**
 * A component backed by static text.
 */
public interface TextComponent extends Component
{
    String getText();
}
