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
package de.cubeisland.engine.formatter.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cubeisland.engine.formatter.formatter.Formatter;

public class FormatContext
{
    private static Map<String, MappedData> mappedData = new HashMap<String, MappedData>();
    private static Map<Class<? extends Formatter>, Map<String, MappedData>> specialMappedData = new HashMap<Class<? extends Formatter>, Map<String, MappedData>>();

    public static void register(MappedData data)
    {
        mappedData.put(data.getKey(), data); // TODO handle multiple
    }

    public static void register(Class<? extends Formatter> formatterClass, MappedData mapped)
    {
        Map<String, MappedData> mappedData = specialMappedData.get(formatterClass);
        if (mappedData == null)
        {
            specialMappedData.put(formatterClass, mappedData = new HashMap<String, MappedData>());
        }
        mappedData.put(mapped.getKey(), mapped);
    }

    private Formatter formatter;

    private Map<String, String> mapped;
    private List<String> flags;

    public <T> T getMapped(String key, Class<T> clazz)
    {
        String value = this.mapped.get(key);
        if (value == null)
        {
            return null;
        }
        Map<String, MappedData> curMappedData = specialMappedData.get(formatter.getClass());
        if (curMappedData != null)
        {
            MappedData data = curMappedData.get(key);
            return (T)data.getData(value);
        }
        return (T)mappedData.get(key).getData(value);
    }

    private String getValue(String key)
    {
        return mapped.get(key);
    }
}
