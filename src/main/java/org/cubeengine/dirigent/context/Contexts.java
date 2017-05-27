package org.cubeengine.dirigent.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.cubeengine.dirigent.formatter.Formatter;

/**
 * Useful helpers to work with contexts.
 */
public class Contexts
{
    /**
     * A reference of a simple empty context.
     */
    public static final Context EMPTY = new Context(Collections.<ContextProperty<?>, Object>emptyMap());

    /**
     * Identification context property for a {@link Locale} which must be considered within the {@link Formatter}
     * implementations.
     */
    public static final ContextProperty<Locale> LOCALE = new ContextProperty<Locale>();

    /**
     * Identification context property for a {@link Locale} which must be considered within the {@link Formatter}
     * implementations.
     */
    public static final ContextProperty<TimeZone> TIMEZONE = new ContextProperty<TimeZone>();

    /**
     * Identification context property for a {@link Locale} which must be considered within the {@link Formatter}
     * implementations.
     */
    public static final ContextProperty<Currency> CURRENCY = new ContextProperty<Currency>();

    /**
     * Creates a new empty context.
     *
     * @return an empty context.
     */
    public static Context createContext()
    {
        return EMPTY;
    }

    /**
     * Creates a new context holding the specified {@link Locale}.
     *
     * @param locale The locale of this context.
     *
     * @return the context.
     */
    public static Context createContext(Locale locale)
    {
        return createContext(locale, TimeZone.getDefault());
    }

    /**
     * Creates a new context holding the specified {@link Locale}.
     *
     * @param locale The locale for this context
     * @param timeZone the timezone for this context
     *
     * @return the context.
     */
    public static Context createContext(Locale locale, TimeZone timeZone)
    {
        return createContext(LOCALE.with(locale), TIMEZONE.with(timeZone), CURRENCY.with(Currency.getInstance(locale)));
    }

    /**
     * Creates a new context with the specified properties.
     *
     * @param mappings The property mappings of the context.
     *
     * @return the context.
     */
    public static Context createContext(PropertyMapping<?>... mappings)
    {
        return createContext(Arrays.asList(mappings));
    }

    public static Context createContext(Collection<PropertyMapping<?>> mappings)
    {
        Map<ContextProperty<?>, Object> properties = new HashMap<ContextProperty<?>, Object>(mappings.size());
        for (final PropertyMapping<?> mapping : mappings)
        {
            properties.put(mapping.property, mapping.value);
        }
        return new Context(properties);
    }
}
