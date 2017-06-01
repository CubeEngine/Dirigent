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

import static org.cubeengine.dirigent.parser.TokenKind.STRING;
import static org.cubeengine.dirigent.parser.TokenKind.STATIC;

public enum TokenType
{
    PLAIN_STRING(STRING),
    ESCAPED_STRING(STRING),
    NUMBER(STRING),
    MACRO_BEGIN(STATIC, Tokenizer.MACRO_BEGIN),
    MACRO_END(STATIC, Tokenizer.MACRO_END),
    LABEL_SEPARATOR(STATIC, Tokenizer.LABEL_SEP),
    SECTION_SEPARATOR(STATIC, Tokenizer.SECTION_SEP),
    VALUE_SEPARATOR(STATIC, Tokenizer.VALUE_SEP);

    public final TokenKind kind;
    public final char character;

    TokenType(TokenKind kind)
    {
        this(kind, '\0');
    }

    TokenType(TokenKind kind, char c)
    {
        this.kind = kind;
        this.character = c;
    }
}
