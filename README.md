![Dirigent](https://github.com/CubeEngine/Dirigent/blob/master/Dirigent.png?raw=true)
=================

Directs your composition

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/dirigent)
[![Build Status](https://travis-ci.org/CubeEngine/Dirigent.svg?branch=master)](https://travis-ci.org/CubeEngine/Dirigent)

A compact formatting framework intended to be used in conjuction with translated messages.

The MessageCompositor#composeMessage(String sourceMessage, Object... messageArguments) method will try to convert every occurence of macros with the following syntax:

 ```{[[<position>:]type[#<label>][:<args>]]}``` or ```{[position]}```

Just using ```{}``` or ```{<position>}``` will result in the macro being replaced by String.valueOf(...) of the Nth messageArgument or the messageArgument at the specified position.

But the real strength of this library stands in its Formatters:

You can register a Macro with multiple "type-names" for a single class or multiple or you could override the isApplicable method to do your own checks.
A Macro will accept multiple arguments separated by `:` which will be provided in the MacroContext.  
It is also possible to map arguments using `<key>=<value>`.  
Readers can be registered to convert these Arguments into an Object.  
A Reader can be registered for all Macros or for a specific Macro only.

Example DateFormatter:

Setup:
First Create a new MessageCompositor for your Locale  
```this.compositor = new DefaultMessageCompositor(myLocale)```    
```this.compositor.registerMacro(new DateFormatter())```  
```this.compositor.registerReader(DateFormatter.class, "pattern", new DateReader())```  
Now the DateFormatter is ready to use, you can also specify the format to use.
```this.compositor.composeMessage("Today is {date:pattern=YYYY-MM-dd}", new Date())```  
The Compositor will now search for any registered Macro/Formatter for "type" accepting a Date.
The DateFormatter will use the DateReader to create a SimpleDateFormatter with the given pattern and then replace the whole macro with the formatted Date.  
   
When tranlating a message with multiple arguments the translater might have to change the order in which the arguments are.  
Using ```{<position>:<type>}``` the translator can specify which argument is needed. Keep in mind positions do start with 0 not with 1!

To further help the translator it is possible to add a label behind the type of the macro e.g.:  
```{date#today}```   
any String is allowed behind the `#` character except `:` and `}` which have to be escaped using `\`  
The label is completly ignored when processing the message.
