package de.cubeisland.engine.formatter;

import de.cubeisland.engine.formatter.formatter.MappedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatContext
{
    private static Map<String, MappedData> mappedData = new HashMap<String, MappedData>();

    public static void register(MappedData data)
    {
       mappedData.put(data.getKey(), data); // TODO handle multiple
    }


    private Map<String, String> mapped;
    private List<String> flags;

    public <T> T getMapped(String key, Class<T> clazz)
    {
        return (T) mappedData.get(key).getData(this.getValue(key));
    }

    private String getValue(String key)
    {
        return mapped.get(key);
    }
}
