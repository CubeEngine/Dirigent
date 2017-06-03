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
package org.cubeengine.dirigent.reflected;

import java.util.Collections;
import java.util.Locale;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.context.Contexts;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.context.Arguments;
import org.cubeengine.dirigent.formatter.reflected.AnnotationMissingException;
import org.cubeengine.dirigent.formatter.reflected.Format;
import org.cubeengine.dirigent.formatter.reflected.InvalidFormatMethodException;
import org.cubeengine.dirigent.formatter.reflected.Names;
import org.cubeengine.dirigent.formatter.reflected.ReflectedFormatter;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.component.Component;
import org.cubeengine.dirigent.parser.component.TextComponent;
import org.cubeengine.dirigent.reflected.ReflectedFormatterTest.PrioFormatter.Type2;
import org.cubeengine.dirigent.reflected.ReflectedFormatterTest.PrioFormatter.TypeImpl;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Tests for the {@link ReflectedFormatter}.
 */
public class ReflectedFormatterTest
{
    @Test(expected = AnnotationMissingException.class)
    public void testWithoutFormatMethods()
    {
        @Names("test")
        class EmptyFormatter extends ReflectedFormatter
        {}
        new EmptyFormatter();
    }

    @Test(expected = AnnotationMissingException.class)
    public void testWithoutNames()
    {
        class UnnamedFormatter extends ReflectedFormatter
        {
            @Format
            public Component format(String s) {return null;}
        }
        new UnnamedFormatter();
    }

    @Test(expected = AnnotationMissingException.class)
    public void testPrivateFormatMethod()
    {
        @Names("test")
        class UnnamedFormatter extends ReflectedFormatter
        {
            @Format
            private Component format(String s) {return null;}
        }
        new UnnamedFormatter();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testInvalidReturnType()
    {
        @Names("test")
        class InvalidReturn extends ReflectedFormatter {
            @Format
            public void test(String s) {}
        }

        new InvalidReturn();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testNoInput()
    {
        @Names("test")
        class NoInputs extends ReflectedFormatter {
            @Format
            public void test() {}
        }

        new NoInputs();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testWrongParamsTwoParameters()
    {
        @Names("test")
        class WrongParams extends ReflectedFormatter {
            @Format
            public Component test(String s, Integer i) {return null;}
        }

        new WrongParams();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testWrongParamsThreeParametersWithContext()
    {
        @Names("test")
        class WrongParams extends ReflectedFormatter {
            @Format
            public Component test(String s, Context c, Integer i) {return null;}
        }

        new WrongParams();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testWrongParamsThreeParametersWithArguments()
    {
        @Names("test")
        class WrongParams extends ReflectedFormatter {
            @Format
            public Component test(String s, Integer i, Arguments a) {return null;}
        }

        new WrongParams();
    }

    @Test(expected = InvalidFormatMethodException.class)
    public void testWrongParamsMoreParameters()
    {
        @Names("test")
        class WrongParams extends ReflectedFormatter {
            @Format
            public Component test(String s, Context c, Arguments a, Integer i) {return null;}
        }

        new WrongParams();
    }

    @Test
    public void testInvokerOnlyWithInput()
    {
        final String msg = "blub";
        final Component component = new OnlyWithInput().process(msg, Contexts.EMPTY, Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(msg, ((TextComponent)component).getText());
    }

    @Test
    public void testInvokerWithContext()
    {
        final String msg = "blub";
        final Locale locale = Locale.GERMANY;
        final Component component = new WithContext().process(msg, Contexts.createContext(locale), Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(msg + locale, ((TextComponent)component).getText());
    }

    @Test
    public void testInvokerWithArguments()
    {
        final String msg = "blub";
        final Arguments arguments = Arguments.create(singletonList("arg"), null);
        final Component component = new WithArguments().process(msg, Contexts.EMPTY, arguments);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(msg + arguments, ((TextComponent)component).getText());
    }

    @Test
    public void testInvokerWithContextAndArguments()
    {
        final String msg = "blub";
        final Locale locale = Locale.GERMANY;
        final Arguments arguments = Arguments.create(singletonList("arg"), null);
        final Component component = new WithContextAndArguments().process(msg, Contexts.createContext(locale), arguments);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(msg + locale + arguments, ((TextComponent)component).getText());
    }

    @Test
    public void testInvokerWithArgumentsAndContext()
    {
        final String msg = "blub";
        final Locale locale = Locale.GERMANY;
        final Arguments arguments = Arguments.create(singletonList("arg"), null);
        final Component component = new WithArgumentsAndContext().process(msg, Contexts.createContext(locale), arguments);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(msg + arguments + locale, ((TextComponent)component).getText());
    }

    @Test
    public void testFormatWithPrio() {
        final TypeImpl typeImpl = new TypeImpl();
        Formatter<Object> formatter = new PrioFormatter();

        Component component = formatter.process(typeImpl, Contexts.EMPTY, Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals(typeImpl.getText(), ((TextComponent)component).getText());

        component = formatter.process(new Type2()
        {
            @Override
            public int getNumber()
            {
                return 34;
            }
        }, Contexts.EMPTY, Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals("34", ((TextComponent)component).getText());
    }

    @Test
    public void testFormatInheritance() {
        Formatter<Object> formatter = new InheritanceFormatter();

        Component component = formatter.process(42, Contexts.EMPTY, Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals("42.0", ((TextComponent)component).getText());

        component = formatter.process(42.41d, Contexts.EMPTY, Arguments.NONE);
        Assert.assertTrue(component instanceof TextComponent);
        Assert.assertEquals("42", ((TextComponent)component).getText());
    }

    @Names("test")
    public static class OnlyWithInput extends ReflectedFormatter
    {
        @Format
        public Component test(String string)
        {
            return new Text(string);
        }
    }

    @Names("test")
    public static class WithContext extends ReflectedFormatter
    {
        @Format
        public Component test(String string, Context context)
        {
            return new Text(string + context.get(Contexts.LOCALE));
        }
    }

    @Names("test")
    public static class WithArguments extends ReflectedFormatter
    {
        @Format
        public Component test(String string, Arguments arguments)
        {
            return new Text(string + arguments);
        }
    }

    @Names("test")
    public static class WithContextAndArguments extends ReflectedFormatter
    {
        @Format
        public Component test(String string, Context context, Arguments arguments)
        {
            return new Text(string + context.get(Contexts.LOCALE) + arguments);
        }
    }

    @Names("test")
    public static class WithArgumentsAndContext extends ReflectedFormatter
    {
        @Format
        public Component test(String string, Arguments arguments, Context context)
        {
            return new Text(string + arguments + context.get(Contexts.LOCALE));
        }
    }

    @Names("test")
    public static class PrioFormatter extends ReflectedFormatter
    {
        @Format(4)
        public Component format(Type1 type1) {
            return new Text(type1.getText());
        }

        @Format
        public Component format(Type2 type2) {
            return new Text(String.valueOf(type2.getNumber()));
        }

        public interface Type1 {
            String getText();
        }

        public interface Type2 {
            int getNumber();
        }

        public static class TypeImpl implements Type1, Type2 {

            @Override
            public String getText()
            {
                return "text";
            }

            @Override
            public int getNumber()
            {
                return 42;
            }
        }
    }

    @Names("test")
    public static class InheritanceFormatter extends ReflectedFormatter {
        @Format
        public Component format(Number number) {
            return new Text(String.valueOf(number.doubleValue()));
        }

        @Format
        public Component format(Double number) {
            return new Text(String.valueOf(number.intValue()));
        }
    }
}
