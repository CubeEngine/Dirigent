/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.formatter.formatter.example;

import de.cubeisland.engine.formatter.context.FormatContext;
import de.cubeisland.engine.formatter.formatter.AbstractFormatter;
import de.cubeisland.engine.formatter.context.MappedData;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class DateFormatter extends AbstractFormatter<Date>
{
    static
    {
        FormatContext.register(DateFormatter.class, new DateData());
    }

    public DateFormatter()
    {
        super(new HashSet<String>(Arrays.asList("", "")));
    }

    @Override
    public String format(Date object, FormatContext flags)
    {
        return flags.getMapped("format", SimpleDateFormat.class).format(object);
    }


    public static class DateData implements MappedData<SimpleDateFormat>
    {

        @Override
        public SimpleDateFormat getData(String raw)
        {
            return new SimpleDateFormat(raw);
        }

        @Override
        public String getKey()
        {
            return "format";
        }
    }
}
