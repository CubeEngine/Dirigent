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
import org.cubeengine.dirigent.formatter.argument.Argument;
import org.cubeengine.dirigent.formatter.argument.Parameter;
import org.cubeengine.dirigent.formatter.argument.Value;
import org.cubeengine.dirigent.parser.element.CompleteMacro;
import org.cubeengine.dirigent.parser.element.Element;
import org.cubeengine.dirigent.parser.element.IndexedDefaultMacro;
import org.cubeengine.dirigent.parser.element.NamedMacro;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.cubeengine.dirigent.parser.Parser.parse;
import static org.cubeengine.dirigent.parser.Parser.unescape;
import static org.cubeengine.dirigent.parser.element.DefaultMacro.DEFAULT_MACRO;
import static org.junit.Assert.assertEquals;

public class ParserTest
{
    public static List<Element> elems(Element... components)
    {
        return Arrays.asList(components);
    }

    public static Element txt(String s)
    {
        return new Text(s);
    }

    public static Element named(String name, Argument... args)
    {
        return new NamedMacro(name, asList(args));
    }

    public static Element indexed(int i)
    {
        return new IndexedDefaultMacro(i);
    }

    public static Element complete(int i, String name, Argument... args)
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

    public static Element err(String s)
    {
        return new InvalidMacro(s);
    }

    @Test
    public void testReadMessage()
    {
        assertEquals(elems(txt("only text")), parse("only text"));
        assertEquals(elems(DEFAULT_MACRO), parse("{}"));
        assertEquals(elems(named("name")), parse("{name}"));
        assertEquals(elems(indexed(0)), parse("{0}"));
        assertEquals(elems(complete(1, "name")), parse("{1:name#with index and comment}"));

        assertEquals(
            elems(complete(1, "name", arg("and parameter"))),
            parse("{1:name#with index and comment:and parameter}"));

        assertEquals(
            elems(complete(1, "name", arg("#parameterwithhash"))),
            parse("{1:name#with index and comment:#parameterwithhash}"));

        assertEquals(
            elems(complete(1, "name", arg("and parameter", "with value"))),
            parse("{1:name#with index and comment:and parameter=with value}"));

        assertEquals(
            elems(complete(1, "name", arg("and parameter", "with value"), arg("multiple"))),
            parse("{1:name#with index and comment:and parameter=with value:multiple}"));

        assertEquals(
            elems(complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more"))),
            parse("{1:name#with index and comment:and parameter=with value:multiple:and one=more}"));

        assertEquals(
            elems(txt("text and a macro "), complete(1, "name", arg("and parameter", "with value"), arg("multiple"), arg("and one", "more")), txt(" more text")),
            parse("text and a macro {1:name#with index and comment:and parameter=with value:multiple:and one=more} more text"));

        assertEquals(
            elems(txt("empty "), named(" ")),
            parse("empty { }"));
    }

    @Test
    public void testInvalidTokens()
    {
        assertEquals(
            elems(txt("illegal macro {starts but wont end")),
            parse("illegal macro {starts but wont end"));

        assertEquals(
            elems(txt("illegal macro {starts:has arguments but wont end")),
            parse("illegal macro {starts:has arguments but wont end"));

        assertEquals(
            elems(txt("illegal macro "), txt("{starts:has arguments }but wont end")),
            parse("illegal macro {starts:has arguments \\}but wont end"));

        assertEquals(
            elems(txt("illegal macro "), txt("{starts#lab}el:has arguments but wont end")),
            parse("illegal macro {starts#lab\\}el:has arguments but wont end"));

        assertEquals(
            elems(txt("illegal macro { text and "), named("second")),
            parse("illegal macro { text and {second}"));

        assertEquals(
            elems(txt("illegal macro {0 text and "), named("second")),
            parse("illegal macro {0 text and {second}"));

        assertEquals(
            elems(txt("illegal macro {text and "), named("second")),
            parse("illegal macro {text and {second}"));

        assertEquals(
            elems(txt("illegal macro {0:text and "), named("second")),
            parse("illegal macro {0:text and {second}"));

        assertEquals(
            elems(txt("illegal macro {0:text#label and "), named("second")),
            parse("illegal macro {0:text#label and {second}"));

        assertEquals(
            elems(txt("illegal macro {0:text#label:arg and "), named("second")),
            parse("illegal macro {0:text#label:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro {text#label:arg and "), named("second")),
            parse("illegal macro {text#label:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro {text#label and "), named("second")),
            parse("illegal macro {text#label and {second}"));

        assertEquals(
            elems(txt("illegal macro {text:arg and "), named("second")),
            parse("illegal macro {text:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro {0:text:arg and "), named("second")),
            parse("illegal macro {0:text:arg and {second}"));
    }

    @Test
    public void testReadMessageWithEscaping()
    {
        assertEquals(
            elems(txt("some escape test {} or {name#label:mdmmd}")),
            parse("some escape test \\{} or \\{name#label:mdmmd}"));

        assertEquals(
            elems(txt("some text with "),
                  named("text", arg("static \\\\ tex:t\\"), arg("moep")),
                  txt("!")),
            parse("some text with {text:static \\\\\\\\ tex\\:t\\\\:moep}!"));

        assertEquals(
            elems(txt("escaping "), named("in", arg("arg"))),
            parse("escaping {in#la\\:b\\el:arg}"));

        assertEquals(
            elems(txt("escaping "), named("in", arg("and#}=:\\arg"))),
            parse("escaping {in#la#be\\}l:and#\\}\\=\\:\\\\arg}"));
    }

    @Test
    public void testEmptyPartsOfAMacro() {
        // TODO have a look at the tests; are they correct?
        assertEquals(
            elems(txt("empty {0:}")),
            parse("empty {0:}"));

        assertEquals(
            elems(txt("empty {name#}")),
            parse("empty {name#}"));

        assertEquals(
            elems(txt("empty {name#moep:}")),
            parse("empty {name#moep:}"));

        assertEquals(
            elems(txt("empty {name:}")),
            parse("empty {name:}"));

        assertEquals(
            elems(txt("empty "), named("name", arg(":"))),
            parse("empty {name::}"));

        assertEquals(
            elems(txt("empty "), named("name")),
            parse("empty {name#:}"));

        assertEquals(
            elems(txt("empty "), named("name", arg("arg"))),
            parse("empty {name#:arg}"));
    }

    @Test
    public void testStripBackslashes()
    {
        assertEquals("\\", convertAndUnescape("\\"));
        assertEquals("sta\\:tic", convertAndUnescape("sta\\:tic"));
        assertEquals("static", convertAndUnescape("static"));
        assertEquals("static \\\\ tex\\:t\\", convertAndUnescape("static \\\\\\\\ tex\\:t\\\\"));
        assertEquals("static \\\\ tex\\:t\\", convertAndUnescape("static \\\\\\\\ tex\\:t\\\\"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMessage()
    {
        parse(null);
    }

    @Test
    public void testIntegerConversion()
    {
        assertEquals(elems(indexed(11)), parse("{11}"));
    }

    @Test
    public void testIntegerValidation()
    {
        assertEquals(elems(named("1a")), parse("{1a}"));
    }

    private static String convertAndUnescape(String s)
    {
        return unescape(s, 0, s.length(), false);
    }
}
