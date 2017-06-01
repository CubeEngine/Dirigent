package org.cubeengine.dirigent.parser;

import java.util.Collections;
import java.util.List;
import org.cubeengine.dirigent.parser.Tokenizer.TokenBuffer;
import org.cubeengine.dirigent.parser.element.Element;

public class Parser
{
    public static List<Element> parse(String message) {
        TokenBuffer buf = Tokenizer.tokenize(message);

        return Collections.emptyList();
    }
}
