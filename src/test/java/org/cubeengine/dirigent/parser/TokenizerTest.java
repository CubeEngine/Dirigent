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
package org.cubeengine.dirigent.parser;

import java.util.Arrays;
import java.util.List;
import org.cubeengine.dirigent.parser.token.CompleteMacro;
import org.cubeengine.dirigent.parser.token.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.token.NamedMacro;
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.parser.token.Token;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.cubeengine.dirigent.parser.Tokenizer.tokenize;
import static org.cubeengine.dirigent.parser.Tokenizer.unescape;
import static org.cubeengine.dirigent.parser.token.DefaultMacro.DEFAULT_MACRO;
import static org.junit.Assert.assertEquals;

public class TokenizerTest
{
    public static List<Token> tokens(Token... components)
    {
        return Arrays.asList(components);
    }

    public static Token txt(String s)
    {
        return new Text(s);
    }

    public static Token named(String name, Argument... args)
    {
        return new NamedMacro(name, asList(args));
    }

    public static Token indexed(int i)
    {
        return new IndexedDefaultMacro(i);
    }

    public static Token complete(int i, String name, Argument... args)
    {
        return new CompleteMacro(i, name, asList(args));
    }

    public static Argument arg(String name)
    {
        return new Value(name);
    }

    public static Argument arg(String name, String val)
    {
        return new Parameter(name, val);
    }

    public static Token err(String s)
    {
        return new InvalidMacro(s);
    }

    @Test
    public void testReadMessage()
    {
        assertEquals(tokens(txt("only text")), tokenize("only text"));
        assertEquals(tokens(DEFAULT_MACRO), tokenize("{}"));
        assertEquals(tokens(named("name")), tokenize("{name}"));
        assertEquals(tokens(indexed(0)), tokenize("{0}"));
        assertEquals(tokens(complete(1, "name")), tokenize("{1:name#with index and comment}"));

        assertEquals(
            tokens(complete(1, "name", arg("and parameter"))),
            tokenize("{1:name#with index and comment:and parameter}"));

        assertEquals(
            tokens(complete(1, "name", arg("#parameterwithhash"))),
            tokenize("{1:name#with index and comment:#parameterwithhash}"));

        assertEquals(
            tokens(complete(1, "name", arg("and parameter", "with value"))),
            tokenize("{1:name#with index and comment:and parameter=with value}"));

        assertEquals(
            tokens(complete(1, "name", arg("and parameter", "with value"), arg("multiple"))),
            tokenize("{1:name#with index and comment:and parameter=with value:multiple}"));

        assertEquals(
            tokens(complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more"))),
            tokenize("{1:name#with index and comment:and parameter=with value:multiple:and one=more}"));

        assertEquals(
            tokens(txt("text and a macro "), complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more")), txt(" more text")),
            tokenize("text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text"));

        assertEquals(
            tokens(txt("illegal macro "), err("{starts but wont end")),
            tokenize("illegal macro {starts but wont end"));

        assertEquals(
            tokens(txt("illegal macro "), err("{starts:has arguments but wont end")),
            tokenize("illegal macro {starts:has arguments but wont end"));
    }

    @Test
    public void testReadMessageWithEscaping()
    {
        assertEquals(
            tokens(txt("some escape test {} or {name#label:mdmmd}")),
            tokenize("some escape test \\{} or \\{name#label:mdmmd}"));

        assertEquals(
            tokens(txt("some text with "),
                named("text", arg("static \\\\ tex:t\\"), arg("moep")),
                txt("!")),
            tokenize("some text with {text:static \\\\\\\\ tex\\:t\\\\:moep}!"));

        assertEquals(
            tokens(txt("escaping "), named("in", arg("arg"))),
            tokenize("escaping {in#la\\:b\\el:arg}"));
    }

    @Test
    public void testStripBackslashes()
    {
        assertEquals("\\", unescape("\\", "=:}\\"));
        assertEquals("sta:tic", unescape("sta\\:tic", "=:}\\"));
        assertEquals("static", unescape("static", "=:}\\"));
        assertEquals("static \\\\ tex:t\\", unescape("static \\\\\\\\ tex\\:t\\\\", "=:}\\"));
        assertEquals("static \\\\ tex\\:t\\", unescape("static \\\\\\\\ tex\\:t\\\\", "=}\\"));
    }
}
