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

import org.junit.Test;

import static org.cubeengine.dirigent.TestHelper.*;
import static org.cubeengine.dirigent.parser.Parser.parse;
import static org.cubeengine.dirigent.parser.element.DefaultMacro.DEFAULT_MACRO;
import static org.junit.Assert.assertEquals;

public class ParserTest
{
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
    public void testParseEmptyMessage() {
        assertEquals(elems(), parse(""));
    }

    @Test
    public void testLabelSeparatorInArguments() {
        assertEquals(
            elems(txt("msg "), named("number", arg("format", "#.0"))),
            parse("msg {number:format=#.0}"));

        assertEquals(
            elems(txt("msg "), named("number", arg("format", "#,###.0"))),
            parse("msg {number:format=#,###.0}"));

        assertEquals(
            elems(txt("msg "), named("number", arg("#,###.0"))),
            parse("msg {number:#,###.0}"));
    }

    @Test
    public void testValueSeparatorInParameter() {
        assertEquals(
            elems(txt("msg "), named("number", arg("param", "arg=arg2"))),
            parse("msg {number:param=arg=arg2}"));
    }

    @Test
    public void testKeyCharactersInTheBeginningOfTheMessage() {
        assertEquals(
            elems(txt("=message")),
            parse("=message"));

        assertEquals(
            elems(txt("}message")),
            parse("}message"));

        assertEquals(
            elems(txt("#message")),
            parse("#message"));

        assertEquals(
            elems(txt(":message")),
            parse(":message"));
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
            elems(txt("illegal macro {starts:has arguments \\}but wont end")),
            parse("illegal macro {starts:has arguments \\}but wont end"));

        assertEquals(
            elems(txt("illegal macro {starts#lab\\}el:has arguments but wont end")),
            parse("illegal macro {starts#lab\\}el:has arguments but wont end"));

        assertEquals(
            elems(txt("illegal macro "), named(" text and {second")),
            parse("illegal macro { text and {second}"));

        assertEquals(
            elems(txt("illegal macro "), named("0 text and {second")),
            parse("illegal macro {0 text and {second}"));

        assertEquals(
            elems(txt("illegal macro "), named("text and {second")),
            parse("illegal macro {text and {second}"));

        assertEquals(
            elems(txt("illegal macro "), complete(0, "text and {second")),
            parse("illegal macro {0:text and {second}"));

        assertEquals(
            elems(txt("illegal macro "), complete(0, "text")),
            parse("illegal macro {0:text#label and {second}"));

        assertEquals(
            elems(txt("illegal macro "), complete(0, "text", arg("arg and {second"))),
            parse("illegal macro {0:text#label:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro "), named("text", arg("arg and {second"))),
            parse("illegal macro {text#label:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro "), named("text")),
            parse("illegal macro {text#label and {second}"));

        assertEquals(
            elems(txt("illegal macro "), named("text", arg("arg and {second"))),
            parse("illegal macro {text:arg and {second}"));

        assertEquals(
            elems(txt("illegal macro "), complete(0, "text", arg("arg and {second"))),
            parse("illegal macro {0:text:arg and {second}"));

        assertEquals(elems(txt("illegal macro {")), parse("illegal macro {"));

        assertEquals(elems(txt("illegal macro {12")), parse("illegal macro {12"));

        assertEquals(elems(txt("illegal macro {12:")), parse("illegal macro {12:"));

        assertEquals(elems(txt("illegal macro {name#")), parse("illegal macro {name#"));

        assertEquals(elems(txt("illegal macro {name#:arg")), parse("illegal macro {name#:arg"));

        assertEquals(elems(txt("illegal macro {name#:arg=")), parse("illegal macro {name#:arg="));
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
        assertEquals(
            elems(txt("empty {0:} empty")),
            parse("empty {0:} empty"));

        assertEquals(
            elems(txt("empty "), named("name"), txt(" empty")),
            parse("empty {name#} empty"));

        assertEquals(
            elems(txt("empty "), named("name", arg("")), txt(" empty")),
            parse("empty {name#moep:} empty"));

        assertEquals(
            elems(txt("empty "), named("name", arg("")), txt(" empty")),
            parse("empty {name:} empty"));

        assertEquals(
            elems(txt("empty "), named("name", arg(""), arg("")), txt(" empty")),
            parse("empty {name::} empty"));

        assertEquals(
            elems(txt("empty "), named("name", arg("")), txt(" empty")),
            parse("empty {name#:} empty"));

        assertEquals(
            elems(txt("empty "), named("name", arg("arg")), txt(" empty")),
            parse("empty {name#:arg} empty"));

        assertEquals(
            elems(txt("empty {name#:=arg} empty")),
            parse("empty {name#:=arg} empty"));
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
        assertEquals(elems(indexed(12341)), parse("{12341}"));
    }

    @Test
    public void testIntegerValidation()
    {
        assertEquals(elems(named("1a")), parse("{1a}"));
        assertEquals(elems(indexed(123)), parse("{123}"));
        assertEquals(elems(named("-123")), parse("{-123}"));
        assertEquals(elems(named("0123")), parse("{0123}"));
        assertEquals(elems(named("123453a4443")), parse("{123453a4443}"));
    }
}
