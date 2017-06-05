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
package org.cubeengine.dirigent.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Arguments} class.
 */
public class ArgumentsTest
{
    private final static String FIRST_KEY = "first-key";
    private final static String FIRST_VALUE = "first-value";
    private final static String SECOND_KEY = "second-key";
    private final static String SECOND_VALUE = "second-value";
    private final static String RANDOM_CASE_VALUE = "RaNdOm-CASe";

    @Test
    public void testCreate()
    {
        Assert.assertEquals(Arguments.NONE, Arguments.create());
    }

    @Test
    public void testCreateWithEmptyArgs()
    {
        Assert.assertEquals(Arguments.NONE,
                            Arguments.create(Collections.<String>emptyList(), Collections.<String, String>emptyMap()));
    }

    @Test
    public void testGetWithIndex()
    {
        final Arguments arguments = create();

        Assert.assertEquals(FIRST_VALUE, arguments.get(0));
        Assert.assertEquals(SECOND_VALUE, arguments.get(1));
    }

    @Test
    public void testGetWithName()
    {
        final Arguments arguments = create();

        Assert.assertEquals(FIRST_VALUE, arguments.get(FIRST_KEY));
        Assert.assertEquals(SECOND_VALUE, arguments.get(SECOND_KEY));
    }

    @Test
    public void testGetOrElse()
    {
        final Arguments arguments = create();

        Assert.assertEquals(FIRST_VALUE, arguments.getOrElse(FIRST_KEY, "default"));
        Assert.assertEquals("default", arguments.getOrElse("blub", "default"));
    }

    @Test
    public void testGetOrElseIndex()
    {
        final Arguments arguments = create();

        Assert.assertEquals("default", arguments.getOrElse(-1, "default"));

        List<String> values = arguments.getValues();
        for (int i = 0; i < values.size(); i++)
        {
            Assert.assertEquals(values.get(i), arguments.getOrElse(i, "default"));
        }

        Assert.assertEquals("default", arguments.getOrElse(values.size(), "default"));
    }

    @Test
    public void testHas()
    {
        final Arguments arguments = create();

        Assert.assertTrue(arguments.has(FIRST_VALUE));
        Assert.assertTrue(arguments.has(SECOND_VALUE));
        Assert.assertTrue(arguments.has(RANDOM_CASE_VALUE));

        Assert.assertFalse(arguments.has("blub"));
        Assert.assertFalse(arguments.has(FIRST_VALUE.toUpperCase()));
        Assert.assertFalse(arguments.has(SECOND_VALUE.toUpperCase()));
    }

    @Test
    public void testHasIgnoreCase()
    {
        final Arguments arguments = create();

        Assert.assertTrue(arguments.hasIgnoringCase(FIRST_VALUE));
        Assert.assertTrue(arguments.hasIgnoringCase(SECOND_VALUE));
        Assert.assertTrue(arguments.hasIgnoringCase(RANDOM_CASE_VALUE));

        Assert.assertFalse(arguments.hasIgnoringCase("blub"));

        Assert.assertTrue(arguments.hasIgnoringCase(FIRST_VALUE.toUpperCase()));
        Assert.assertTrue(arguments.hasIgnoringCase(SECOND_VALUE.toUpperCase()));
    }

    private Arguments create()
    {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(FIRST_KEY, FIRST_VALUE);
        params.put(SECOND_KEY, SECOND_VALUE);

        final List<String> values = Arrays.asList(FIRST_VALUE, SECOND_VALUE, RANDOM_CASE_VALUE);

        return Arguments.create(values, params);
    }
}
