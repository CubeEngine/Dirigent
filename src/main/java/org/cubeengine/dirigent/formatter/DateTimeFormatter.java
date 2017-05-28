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
package org.cubeengine.dirigent.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.context.Contexts;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;

import static org.cubeengine.dirigent.context.Contexts.LOCALE;

/**
 * <p>
 * The date time formatter formats a {@link Date} object with a {@link DateFormat}. The class has a {@link Mode} which
 * can be passed as a constructor parameter. This mode specifies whether the instance formats a {@link Date} object to a
 * date, a time or a date time representation by default. The default constructors uses the data time representation.
 * </p>
 * <p>
 * In a message it's possible to control the format with two types of options. The first possibility is specifying a
 * "format" parameter. This parameter creates a {@link SimpleDateFormat} with the specified format. Furthermore it's
 * possible to specify one of the flags "short", "medium", "long" and "full". This controls the style of a
 * {@link DateFormat} instance. Specified as a flag this style is used for date and time. With one of the parameters
 * "date" and "time" it's possible to control both styles independently. Furthermore the {@link java.util.Locale} is
 * respected and passed to the format.
 * </p>
 */
public class DateTimeFormatter extends AbstractFormatter<Date>
{
    /**
     * The name of the format parameter.
     */
    static final String FORMAT_PARAM_NAME = "format";
    /**
     * The name of the date parameter.
     */
    static final String DATE_PARAM_NAME = "date";
    /**
     * The name of the time parameter.
     */
    static final String TIME_PARAM_NAME = "time";

    /**
     * The short style flag label.
     */
    static final String SHORT_STYLE = "short";
    /**
     * The medium style flag label.
     */
    static final String MEDIUM_STYLE = "medium";
    /**
     * The long style flag label.
     */
    static final String LONG_STYLE = "long";
    /**
     * The full style flag label.
     */
    static final String FULL_STYLE = "full";

    /**
     * The mode of this formatter.
     */
    private final Mode mode;

    /**
     * Constructor. Initializes this formatter with a few default names and the mode {@link Mode#DATE_TIME}.
     */
    public DateTimeFormatter()
    {
        this(Mode.DATE_TIME, "datetime");
    }

    /**
     * Constructor.
     *
     * @param mode The default mode of the formatter.
     * @param names The names triggering this formatter.
     */
    public DateTimeFormatter(Mode mode, String... names)
    {
        super(names);
        this.mode = mode;
    }

    @Override
    protected Component format(Date param, Context context, Arguments args)
    {
        return new Text(parseDateToString(param, context, args));
    }

    /**
     * Parses the given date to a string depending on the context.
     *
     * @param date The date to parse.
     * @param context The context to use.
     * @param args The arguments of the macro.
     *
     * @return The number as a string.
     */
    protected String parseDateToString(Date date, Context context, Arguments args)
    {
        final DateFormat dateFormat = parseFormatter(context, args);
        dateFormat.setTimeZone(context.get(Contexts.TIMEZONE));
        return dateFormat.format(date);
    }

    /**
     * Parses the {@link DateFormat} to use from the context arguments.
     *
     * @param context The context.
     * @param args The arguments of the macro.
     *
     * @return the {@link DateFormat}.
     */
    private DateFormat parseFormatter(Context context, Arguments args)
    {
        final String format = args.get(FORMAT_PARAM_NAME);
        final Locale locale = context.get(LOCALE);
        if (format != null)
        {
            return new SimpleDateFormat(format, locale);
        }

        final int defaultFormatStyle = parseDateFormatStyle(args);
        final int dateFormatStyle = parseDateFormatStyle(args.get(DATE_PARAM_NAME), defaultFormatStyle);
        final int timeFormatStyle = parseDateFormatStyle(args.get(TIME_PARAM_NAME), defaultFormatStyle);

        if (Mode.DATE_TIME.equals(mode))
        {
            return DateFormat.getDateTimeInstance(dateFormatStyle, timeFormatStyle, locale);
        }
        else if (Mode.DATE.equals(mode))
        {
            return DateFormat.getDateInstance(dateFormatStyle, locale);
        }
        else if (Mode.TIME.equals(mode))
        {
            return DateFormat.getTimeInstance(timeFormatStyle, locale);
        }
        return DateFormat.getInstance();
    }

    /**
     * Parses the style of the {@link DateFormat} from a string label.
     *
     * @param text The string label.
     * @param defaultStyle The default style.
     *
     * @return the parsed style or the default style if it's an incorrect style.
     */
    private int parseDateFormatStyle(String text, int defaultStyle)
    {
        if (text == null)
        {
            return defaultStyle;
        }

        if (SHORT_STYLE.equalsIgnoreCase(text))
        {
            return DateFormat.SHORT;
        }
        else if (MEDIUM_STYLE.equalsIgnoreCase(text))
        {
            return DateFormat.MEDIUM;
        }
        else if (LONG_STYLE.equalsIgnoreCase(text))
        {
            return DateFormat.LONG;
        }
        else if (FULL_STYLE.equalsIgnoreCase(text))
        {
            return DateFormat.FULL;
        }
        return defaultStyle;
    }

    /**
     * Parses the default style of the {@link DateFormat} from context labels.
     *
     * @param args The arguments of the macro.
     *
     * @return The id of the style.
     */
    private int parseDateFormatStyle(Arguments args)
    {
        if (args.has(SHORT_STYLE))
        {
            return DateFormat.SHORT;
        }
        else if (args.has(MEDIUM_STYLE))
        {
            return DateFormat.MEDIUM;
        }
        else if (args.has(LONG_STYLE))
        {
            return DateFormat.LONG;
        }
        else if (args.has(FULL_STYLE))
        {
            return DateFormat.FULL;
        }
        return DateFormat.DEFAULT;
    }

    /**
     * The mode of the formatter.
     */
    public enum Mode
    {
        /**
         * Represents a {@link Date} object with the date and the time.
         */
        DATE_TIME,
        /**
         * Represents a {@link Date} object only with the date.
         */
        DATE,
        /**
         * Represents a {@link Date} object only with the time.
         */
        TIME
    }
}
