package org.cubeengine.dirigent.formatter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.cubeengine.dirigent.Component;
import org.cubeengine.dirigent.parser.component.Text;
import org.cubeengine.dirigent.parser.component.macro.argument.Argument;
import org.cubeengine.dirigent.parser.component.macro.argument.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link StringFormatter}.
 */
public class StringFormatterTest
{
    private StringFormatter stringFormatter = new StringFormatter();

    @Test
    public void testFormat()
    {
        checkFormat("42", 42, Locale.GERMANY, null);
        checkFormat("someTHING", "someTHING", Locale.GERMANY, null);
        checkFormat("something", "someTHING", Locale.GERMANY, StringFormatter.LOWERCASE_FLAG);
        checkFormat("SOMETHING", "someTHING", Locale.GERMANY, StringFormatter.UPPERCASE_FLAG);
    }

    private void checkFormat(final String expected, final Object object, final Locale locale, final String flag)
    {
        final List<Argument> arguments;
        if (flag == null)
        {
            arguments = Collections.emptyList();
        }
        else
        {
            arguments = Collections.<Argument>singletonList(new Value(flag));
        }

        final Context context = new Context(locale).with(arguments);
        final Component component = stringFormatter.format(object, context);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getString());
    }
}
