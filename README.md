![Dirigent](https://github.com/CubeEngine/Dirigent/blob/master/Dirigent.png?raw=true)
=================

Directs your composition

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent)
[![Build Status](https://travis-ci.org/CubeEngine/Dirigent.svg?branch=master)](https://travis-ci.org/CubeEngine/Dirigent)

A compact formatting framework intended to be used in conjuction with translated messages.

The Dirigent#compose(String source, Object... args) method will try to convert every occurrence of macros with the following syntax:

 ```{[[<position>:]type[#<label>][:<args>]]}``` or ```{[position]}```

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
