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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.context.Contexts;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.Text;

import static org.cubeengine.dirigent.context.Contexts.LOCALE;

/**
 * The number formatter formats a {@link Number} object with a {@link NumberFormat}. The class has a {@link Mode} which
 * can be passed as a constructor parameter. This mode specifies which kind of {@link NumberFormat} shall be used by
 * default. It is possible to overwrite this default behaviour using a flag on the message macro. If the mode isn't
 * specified this formatter works similar to the {@link java.text.MessageFormat#format(Object)} method with the macro
 * {@code number}.
 *
 * In a message it's possible to control the format with two types of options. The first possibility is specifying a
 * "format" parameter. This parameter creates a {@link DecimalFormat} object with the specified format. Furthermore it's
 * possible to specify one of the flags "integer", "currency", "percent". This information loads the
 * {@link NumberFormat} via {@link NumberFormat#getIntegerInstance()}, {@link NumberFormat#getCurrencyInstance()} or
 * {@link NumberFormat#getPercentInstance()}. It is the same functionality as handled by the {@link Mode}. The default
 * behaviour loads the format with the {@link NumberFormat#getInstance()} method. Furthermore the locale is respected
 * at this point and passed to the format.
 */
public class NumberFormatter extends AbstractFormatter<Number>
{
    /**
     * The name of the format parameter.
     */
    static final String FORMAT_PARAM_NAME = "format";

    /**
     * The flag of the integer mode.
     */
    static final String INTEGER_MODE_FLAG = "integer";
    /**
     * The flag of the currency mode.
     */
    static final String CURRENCY_MODE_FLAG = "currency";
    /**
     * The flag of the percent mode.
     */
    static final String PERCENT_MODE_FLAG = "percent";

    /**
     * The default mode of this number formatter.
     */
    private final Mode defaultMode;

    /**
     * Constructor. Initializes this formatter with a few default names.
     */
    public NumberFormatter()
    {
        this(null, "number", "decimal", "double", "float");
    }

    /**
     * Constructor.
     *
     * @param mode The default mode of the formatter. May be null.
     * @param names The names triggering this formatter.
     */
    public NumberFormatter(Mode mode, String... names)
    {
        super(names);
        this.defaultMode = mode;
    }

    @Override
    protected Component format(Number input, Context context, Arguments args)
    {
        return new Text(parseNumberToString(input, context, args));
    }

    /**
     * Parses the given number to a string depending on the context.
     *
     * @param number The number to parse.
     * @param context The context to use.
     * @param args The arguments of the macro.
     *
     * @return The number as a string.
     */
    protected String parseNumberToString(Number number, Context context, Arguments args)
    {
        final NumberFormat numberFormat = parseFormatter(context, args);

        final Currency currency = context.get(Contexts.CURRENCY);
        if (currency != null)
        {
            numberFormat.setCurrency(currency);
        }

        return numberFormat.format(number);
    }

    /**
     * Parses the {@link NumberFormat} to use from the context arguments.
     *
     * @param context The context.
     * @param args The arguments of the macro.
     *
     * @return the {@link NumberFormat}.
     */
    private NumberFormat parseFormatter(Context context, Arguments args)
    {
        final String format = args.get(FORMAT_PARAM_NAME);
        final Locale locale = context.get(LOCALE);
        if (format != null)
        {
            return new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale));
        }

        final Mode mode = Mode.loadFromContext(args, this.defaultMode);
        if (Mode.INTEGER.equals(mode))
        {
            return NumberFormat.getIntegerInstance(locale);
        }
        if (Mode.CURRENCY.equals(mode))
        {
            return NumberFormat.getCurrencyInstance(locale);
        }
        if (Mode.PERCENT.equals(mode))
        {
            return NumberFormat.getPercentInstance(locale);
        }
        return NumberFormat.getInstance(locale);
    }

    /**
     * The mode of the number format.
     */
    public enum Mode
    {
        /**
         * Loads an integer instance of a {@link NumberFormat}.
         */
        INTEGER,
        /**
         * Loads a currency instance of a {@link NumberFormat}.
         */
        CURRENCY,
        /**
         * Loads a percent instance of a {@link NumberFormat}.
         */
        PERCENT;

        /**
         * Loads the mode from the formatter context or returns the default mode if the context doesn't specify one.
         *
         * @param args The arguments of the macro.
         * @param defaultMode The default mode.
         *
         * @return the loaded mode.
         */
        private static Mode loadFromContext(Arguments args, Mode defaultMode)
        {
            if (args.has(INTEGER_MODE_FLAG))
            {
                return INTEGER;
            }
            if (args.has(CURRENCY_MODE_FLAG))
            {
                return CURRENCY;
            }
            if (args.has(PERCENT_MODE_FLAG))
            {
                return PERCENT;
            }
            return defaultMode;
        }
    }
}
