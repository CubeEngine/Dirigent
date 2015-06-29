package de.cubeisland.engine.messagecompositor.parser;

import org.junit.Test;

public class MessageParserTest
{
    @Test
    public void testReadMessage() throws Exception
    {
        MessageParser.readMessage("only text");
        MessageParser.readMessage("{}");
        MessageParser.readMessage("{name}");
        MessageParser.readMessage("{0}");
        MessageParser.readMessage("{1:name#with index and comment}");
        MessageParser.readMessage("{1:name#with index and comment:and parameter}");
        MessageParser.readMessage("{1:name#with index and comment:and parameter=with value}");
        MessageParser.readMessage("{1:name#with index and comment:and parameter=with value:multiple}");
        MessageParser.readMessage("{1:name#with index and comment:and parameter=with value:multiple:and one=more}");
        MessageParser.readMessage("text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text");
        MessageParser.readMessage("text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text");
    }
}