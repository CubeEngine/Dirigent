/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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
package de.cubeisland.engine.messagecompositor;

import java.util.Locale;

import de.cubeisland.engine.messagecompositor.macro.Macro;
import de.cubeisland.engine.messagecompositor.macro.PostProcessor;
import de.cubeisland.engine.messagecompositor.macro.Reader;

/**
 * A message-compositor processing macros {[[<position>:]type[#<label>][:<args>]]} or just {[pos]}
 */
public interface MessageCompositor
{
    /**
     * Searches for Macros in the sourceMessages and process all found Macros
     *
     * @param sourceMessage    the sourceMessage
     * @param messageArguments the messageArguments
     *
     * @return the processed String
     */
    String composeMessage(String sourceMessage, Object... messageArguments);

    /**
     * Searches for Macros in the sourceMessages and process all found Macros
     *
     * @param locale           the locale
     * @param sourceMessage    the sourceMessage
     * @param messageArguments the messageArguments
     *
     * @return the processed String
     */
    String composeMessage(Locale locale, String sourceMessage, Object... messageArguments);

    /**
     * Registers a Macro for its names
     *
     * @param macro the macro to register
     *
     * @return fluent interface
     */
    MessageCompositor registerMacro(Macro macro);

    /**
     * Registers a Macro for its names
     *
     * @param macro     the macro to register
     * @param asDefault if true registers the macro as default too
     *
     * @return fluent interface
     */
    MessageCompositor registerMacro(Macro macro, boolean asDefault);

    /**
     * Registers a Macro to be used if no type is given
     *
     * @param macro the macro to register
     *
     * @return fluent interface
     */
    MessageCompositor registerDefaultMacro(Macro macro);

    /**
     * Registers a Reader for a specific key
     *
     * @param key    the key
     * @param reader the reader
     *
     * @return fluent interface
     */
    MessageCompositor registerReader(String key, Reader reader);

    /**
     * Registers a Reader for a specific key and macro
     *
     * @param macroClass the macros class
     * @param key        the key
     * @param reader     the reader
     *
     * @return fluent interface
     */
    MessageCompositor registerReader(Class<? extends Macro> macroClass, String key, Reader reader);

    /**
     * Regiters a default Reader for a specific Macro
     *
     * @param macroClass the macros class
     * @param reader     the the reader
     *
     * @return fluent interface
     */
    MessageCompositor registerDefaultReader(Class<? extends Macro> macroClass, Reader reader);

    /**
     * Reads a value for a key
     *
     * @param key   the key
     * @param value the value to read
     * @param clazz the class to cast into
     *
     * @return the read value or null
     */
    <T> T read(String key, String value, Class<T> clazz);

    /**
     * Reads a value for a key and macro
     *
     * @param macro the macro
     * @param key   the key
     * @param value the value to read
     * @param clazz the class to cast into
     *
     * @return the read value or null
     */
    <T> T read(Macro macro, String key, String value, Class<T> clazz);

    /**
     * Reads a value for a macro
     *
     * @param macro the macro
     * @param value the value to read
     * @param clazz the class to cast into
     *
     * @return the read value or null
     */
    <T> T read(Macro macro, String value, Class<T> clazz);

    /**
     * Adds a default post processor to this compositor
     *
     * @param postProcessor the post processor to add
     * @return fluent interface
     */
    MessageCompositor addDefaultPostProcessor(PostProcessor postProcessor);
}
