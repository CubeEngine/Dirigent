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
package org.cubeengine.dirigent.parser;

import java.util.Arrays;
import java.util.List;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.Message;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.CompleteMacro;
import org.cubeengine.dirigent.parser.component.macro.IllegalMacro;
import org.cubeengine.dirigent.parser.component.macro.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.component.macro.NamedMacro;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Flag;
import org.cubeengine.dirigent.parser.component.macro.argument.Parameter;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.cubeengine.dirigent.parser.Parser.parseMessage;
import static org.cubeengine.dirigent.parser.component.macro.DefaultMacro.DEFAULT_MACRO;
import static org.junit.Assert.assertEquals;

public class ParserTest
{
    public static Message msg(Component... components)
    {
        return new Message(components(components));
    }

    public static List<Component> components(Component... components)
    {
        return Arrays.asList(components);
    }

    public static Component txt(String s)
    {
        return new Text(s);
    }

    public static Component named(String name, Argument... args)
    {
        return new NamedMacro(name, asList(args));
    }

    public static Component indexed(int i)
    {
        return new IndexedDefaultMacro(i);
    }

    public static Component complete(int i, String name, Argument... args)
    {
        return new CompleteMacro(i, name, asList(args));
    }

    public static Argument arg(String name)
    {
        return new Flag(name);
    }

    public static Argument arg(String name, String val)
    {
        return new Parameter(name, val);
    }

    public static Component err(String s, String err)
    {
        return new IllegalMacro(s, err);
    }

    @Test
    public void testReadMessage() throws Exception
    {
        assertEquals(msg(txt("only text")), parseMessage("only text"));
        assertEquals(msg(DEFAULT_MACRO), parseMessage("{}"));
        assertEquals(msg(named("name")), parseMessage("{name}"));
        assertEquals(msg(indexed(0)), parseMessage("{0}"));
        assertEquals(msg(complete(1, "name")), parseMessage("{1:name#with index and comment}"));

        assertEquals(
            msg(complete(1, "name", arg("and parameter"))),
            parseMessage("{1:name#with index and comment:and parameter}"));

        assertEquals(
            msg(complete(1, "name", arg("#parameterwithhash"))),
            parseMessage("{1:name#with index and comment:#parameterwithhash}"));

        assertEquals(
            msg(complete(1, "name", arg("and parameter", "with value"))),
            parseMessage("{1:name#with index and comment:and parameter=with value}"));

        assertEquals(
            msg(complete(1, "name", arg("amd parameter", "with value"), arg("multiple"))),
            parseMessage("{1:name#with index and comment:and parameter=with value:multiple}"));

        assertEquals(
            msg(complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more"))),
            parseMessage("{1:name#with index and comment:and parameter=with value:multiple:and one=more}"));

        assertEquals(
            msg(txt("text and a macro "), complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more")), txt(" more text")),
            parseMessage("text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text"));

        assertEquals(
            msg(txt("illegal macro "), err("{starts but wont end", "Encountered macro start, but no valid macro followed.")),
            parseMessage("illegal macro {starts but wont end"));

        assertEquals(
            msg(txt("illegal macro "), err("{starts:has arguments but wont end", "Encountered macro start, but no valid macro followed.")),
            parseMessage("illegal macro {starts:has arguments but wont end"));
    }
}