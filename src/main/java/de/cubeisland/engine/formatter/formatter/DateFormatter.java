package de.cubeisland.engine.formatter.formatter;

import de.cubeisland.engine.formatter.FormatContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter extends AbstractFormatter<Date>
{
    static
    {
        FormatContext.register(new DateData());
    }


    @Override
    public String format(Date object, FormatContext flags)
    {
        return flags.getMapped("format", SimpleDateFormat.class).format(object);
    }


    public static class DateData implements MappedData<SimpleDateFormat>
    {

        @Override
        public SimpleDateFormat getData(String raw) {
            return new SimpleDateFormat(raw);
        }

        @Override
        public String getKey() {
            return "format";
        }
    }
}
