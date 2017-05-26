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
 * Tests the {@link StaticTextFormatter}.
 */
public class StaticTextFormatterTest
{
    private StaticTextFormatter staticTextFormatter = new StaticTextFormatter();

    @Test
    public void testFormat()
    {
        checkFormat(null, Locale.GERMANY, null);
        checkFormat("some Text", Locale.GERMANY, "some Text");
    }

    private void checkFormat(final String expected, final Locale locale, final String argument)
    {
        final List<Argument> arguments;
        if (argument == null)
        {
            arguments = Collections.emptyList();
        }
        else
        {
            arguments = Collections.<Argument>singletonList(new Value(argument));
        }

        final Context context = new Context(locale).with(arguments);
        final Component component = staticTextFormatter.format(context);

        Assert.assertTrue(component instanceof Text);
        Assert.assertEquals(expected, ((Text)component).getString());
    }
}
