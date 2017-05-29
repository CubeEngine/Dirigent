![Dirigent](https://github.com/CubeEngine/Dirigent/blob/master/Dirigent.png?raw=true)
=================

Directs your composition

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent)
[![Build Status](https://travis-ci.org/CubeEngine/Dirigent.svg?branch=master)](https://travis-ci.org/CubeEngine/Dirigent)

little overview! finds macros in a text and replaces them with input parameters

("A compact formatting framework intended to be used in conjunction with translated messages.

The `Dirigent#compose(Context context, String source, Object... inputs)` method will try to convert every occurrence of macros in the message with a parameter input.")

# Macro

In this context macros follow this syntax:

 ```{[[<position>:]type[#<label>][:<args>]]}``` or just ```{[position]}```
 
 description of all macro components
 a few example macros with my examples from master thesis

# Process

Here describe the internal process of the AbstractDirigent class a little
- show main methods (with context and empty context)
- Split messages into tokens Text, Macro 
- convert tokens to components: Text, ResolvedMacro (Formatter + PostProcessors), UnresolvedMacro (two types)
- composing the components part of sub implementations (like BuilderDirigent which is described later)

# Context

what is the context, what can it be used for, where is it important?
what kind of properties are available already? Contexts class

# Formatter

explain formatter, default formatter for default macro
show abstractformatter und reflectedformatter for easy usage

## Available Formatters

dirigent provides a few formatters already; They all provide default macro names, but they can be changed with the constructor

### StringFormatter

explanation

flags: uppercase, lowercase
context-properties: none
default-names: blub

### NumberFormatter

explanation

- flags: integer, currency, percent
- params: format
- context-properties: LOCALE, CURRENCY
- default-names: blub

#### IntegerFormatter

starts in integer mode by default

- default-names: blub

#### CurrencyFormatter

starts in currency mode by default

- default-names: blub

#### PercentFormatter

starts in percent mode by default

- default-names: blub

### DateTimeFormatter

explanation

- flags: short, medium, long, full
- params: format, date, time
- context-properties: LOCALE, TIMEZONE
- default-names: blub

#### DateFormatter

explanation

- default-names: blub

#### TimeFormatter

explanation

- default-names: blub

### StaticTextFormatter

explanation

- args: text
- params: none
- context-properties: none
- default-names: text

# Post Processors

what is it? How can they be used? dirigent + formatter

# BuilderDirigent

composes a message with a builder

StaticBuilderDirigent

how to control components, how to handle own components from own formatter

---------

# Old Stuff

Just using ```{}``` or ```{<position>}``` will result in the macro being replaced by String.valueOf(...) of the Nth messageArgument or the messageArgument at the specified position.

But the real strength of this library stands in its Formatter:

You can register a Macro with multiple "type-names" for a single class or multiple or you could override the isApplicable method to do your own checks.
A Macro will accept multiple arguments separated by `:` which will be provided in the MacroContext.  
It is also possible to map arguments using `<key>=<value>`.

Example DateFormatter:

Setup:
First Create a new Dirigent and register the formatter.  
```this.dirigent = new StringBuilderDirigent()```    
```this.dirigent.registerFormatter(new DateFormatter())```  
Now the DateFormatter is ready to use, you can also specify the format to use.
```this.dirigent.compose("Today is {date:format=yyyy-MM-dd}", new Date())```  
The Dirigent will now search for any registered Formatter for "type" accepting a Date.
The DateFormatter will use format argument to create a SimpleDateFormatter with the specified format and then replace the whole macro with the formatted Date.  
   
When translating a message with multiple arguments the translator might have to change the order in which the arguments are.  
Using ```{<position>:<type>}``` the translator can specify which argument is needed. Keep in mind positions do start with 0 not with 1!

To further help the translator it is possible to add a label behind the type of the macro e.g.:  
```{date#today}```   
any String is allowed behind the `#` character except `:` and `}` which have to be escaped using `\`  
The label is completely ignored when processing the message.
