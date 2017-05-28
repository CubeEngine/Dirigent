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
package org.cubeengine.dirigent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cubeengine.dirigent.context.Context;
import org.cubeengine.dirigent.context.Contexts;
import org.cubeengine.dirigent.formatter.ConstantFormatter;
import org.cubeengine.dirigent.formatter.DefaultFormatter;
import org.cubeengine.dirigent.formatter.Formatter;
import org.cubeengine.dirigent.formatter.PostProcessor;
import org.cubeengine.dirigent.formatter.argument.Arguments;
import org.cubeengine.dirigent.parser.MacroResolutionResult;
import org.cubeengine.dirigent.parser.MacroResolutionState;
import org.cubeengine.dirigent.parser.Text;
import org.cubeengine.dirigent.parser.Tokenizer;
import org.cubeengine.dirigent.parser.component.ComponentGroup;
import org.cubeengine.dirigent.parser.component.ResolvedMacro;
import org.cubeengine.dirigent.parser.component.UnresolvableMacro;
import org.cubeengine.dirigent.parser.token.Indexed;
import org.cubeengine.dirigent.parser.token.Macro;
import org.cubeengine.dirigent.parser.token.NamedMacro;
import org.cubeengine.dirigent.parser.token.Token;

/**
 * Basic implementation of Dirigent providing:
 * - Parsing the source message but not composing the final message components
 * - Formatters and PostProcessors
 */
public abstract class AbstractDirigent<MessageT> implements Dirigent<MessageT>
{
    /**
     * The registered formatter.
     */
    private Map<String, List<Formatter<?>>> formatters = new HashMap<String, List<Formatter<?>>>();
    /**
     * The attached post processors.
     */
    private List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();

    protected AbstractDirigent()
    {
        this(new DefaultFormatter());
    }

    protected AbstractDirigent(Formatter<?> defaultFormatter)
    {
        registerFormatter(defaultFormatter);
    }

    @Override
    public MessageT compose(String source, Object... inputs)
    {
        return this.compose(Contexts.createContext(), source, inputs);
    }

    @Override
    public MessageT compose(Context context, String source, Object... inputs)
    {
        List<Token> tokens = Tokenizer.tokenize(source);
        ComponentGroup message = resolve(tokens, context, inputs);
        return compose(message, context);
    }

    /**
     * Composes the parsed {@link ComponentGroup} into the final form.
     *
     * @param componentGroup A component group holding the entire message components.
     * @param context The compose context.
     *
     * @return the composed message.
     */
    protected abstract MessageT compose(ComponentGroup componentGroup, Context context);

    @Override
    public MacroResolutionResult findFormatter(String name, Object input)
    {
        List<Formatter<?>> list = this.formatters.get(name);
        if (list == null)
        {
            return MacroResolutionResult.UNKNOWN_NAME;
        }
        for (Formatter<?> formatter : list)
        {
            if (formatter.isApplicable(input))
            {
                return new MacroResolutionResult(MacroResolutionState.OK, formatter);
            }
        }
        return MacroResolutionResult.NONE_APPLICABLE;
    }

    @Override
    public Dirigent addPostProcessor(PostProcessor postProcessor)
    {
        postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public Dirigent registerFormatter(Formatter<?> formatter)
    {
        for (String name : formatter.names())
        {
            List<Formatter<?>> list = this.formatters.get(name);
            if (list == null)
            {
                list = new ArrayList<Formatter<?>>();
                formatters.put(name, list);
            }
            list.add(formatter);
        }
        return this;
    }

    /**
     * Iterates through the provided {@link Token}s and converts them to {@link Component}s. Therefore the method uses
     * the registered {@link Formatter} and runs global {@link PostProcessor}s.
     *
     * @param tokens The parsed tokens.
     * @param context The compose context.
     * @param inputs The message input parameters.
     *
     * @return A {@link ComponentGroup} holding all the {@link Component}s representing the input {@link Token}s.
     */
    @SuppressWarnings("unchecked")
    private ComponentGroup resolve(List<Token> tokens, Context context, Object[] inputs)
    {
        if (tokens.isEmpty())
        {
            return ComponentGroup.EMPTY;
        }

        List<Component> list = new ArrayList<Component>();
        int implicitArgCounter = 0;

        for (Token token : tokens)
        {
            Component out;
            Arguments arguments = Arguments.NONE;
            if (token instanceof Text)
            {
                out = (Component)token;
            }
            else if (token instanceof Macro)
            {
                Macro macro = (Macro)token;

                int argIndex;
                boolean explicitIndex;
                if (macro instanceof Indexed)
                {
                    argIndex = ((Indexed)token).getIndex();
                    explicitIndex = true;
                }
                else
                {
                    argIndex = implicitArgCounter;
                    explicitIndex = false;
                }

                // Default macros will not have a name
                String name = null;
                if (macro instanceof NamedMacro)
                {
                    NamedMacro named = (NamedMacro)macro;
                    name = named.getName();
                    arguments = named.getArgs();
                }

                // may be null because it might be a constant macro
                Object input = argIndex < inputs.length ? inputs[argIndex] : null;
                MacroResolutionResult res = this.findFormatter(name, input);
                Formatter formatter = res.getFormatter();
                boolean isConstant = formatter instanceof ConstantFormatter;

                if (res.isOK())
                {
                    out = new ResolvedMacro((Formatter<Object>)formatter, isConstant ? null : input, context,
                                            arguments);
                }
                else
                {
                    out = new UnresolvableMacro(macro, input, res.getState());
                }

                if (!explicitIndex && !isConstant)
                {
                    implicitArgCounter++;
                }
            }
            else
            {
                throw new IllegalStateException(
                    "The message contains Tokens that are not Text or Macro: " + token.getClass().getName());
            }

            list.add(applyPostProcessors(out, context, arguments));
        }

        return new ComponentGroup(list);
    }

    /**
     * Executes all attached {@link PostProcessor}s to process the specified {@link Component}.
     *
     * @param in The component to process.
     * @param context The compose context.
     * @param args The macro arguments.
     *
     * @return The processed component.
     */
    private Component applyPostProcessors(Component in, Context context, Arguments args)
    {
        Component out = in;

        for (final PostProcessor postProcessor : postProcessors)
        {
            out = postProcessor.process(out, context, args);
        }

        return out;
    }
}
