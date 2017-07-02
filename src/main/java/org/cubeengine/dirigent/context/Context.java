package org.cubeengine.dirigent.context;

import java.util.Locale;

/**
 * This interface specifies a context of a compose process which is triggered by the
 * {@link org.cubeengine.dirigent.Dirigent#compose(Context, String, Object...)} method. This might include a
 * {@link Locale} or other information which can be used by the formatters to buildText proper messages.
 */
public interface Context
{
    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     *
     * @param key the context property
     * @param <K> the type which is identified by the property
     *
     * @return the value of the context property.
     */
    <K> K get(ContextProperty<K> key);

    /**
     * Returns the value of a context property which is identified with the specified {@link ContextProperty} object.
     * If the value isn't specified the provided default value will be returned.
     *
     * @param key The context property
     * @param defaultProvider the default value provider
     * @param <K> The type which is identified by the property
     *
     * @return the value of the context property or the default value if it's not specified.
     */
    <K> K getOrElse(ContextProperty<K> key, DefaultProvider<K> defaultProvider);

    /**
     * Sets a new property and returns a new immutable context.
     *
     * @param key the key
     * @param value the value
     * @param <K> the key type
     * @return the new context instance
     */
    <K> Context set(ContextProperty<K> key, K value);

    /**
     * Sets new properties and returns a new immutable context.
     *
     * @param mappings the new mappings
     * @return the new context instance
     */
    Context set(PropertyMapping<?>... mappings);
}
