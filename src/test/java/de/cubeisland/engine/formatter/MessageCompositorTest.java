package de.cubeisland.engine.formatter;

import junit.framework.TestCase;

public class MessageCompositorTest extends TestCase
{
    public void testComposeMessage()
    {
        // TODO proper tests
        MessageCompositor compositor = new MessageCompositor();
        System.out.println("\t" + compositor.composeMessage("Player {player} not found!", "<playername>") + "\n");
        System.out.println("\t" + compositor.composeMessage("Three {2:string} One {regular} Two {1:number}", 1, 2, "3") + "\n");
        System.out.println("\t" + compositor.composeMessage("Easy format {} ; only pos format {2} ; Easy format {}", "is easy", "again", "here") + "\n");
        System.out.println("\t" + compositor.composeMessage("{:}{zahl1}::{:{2:zahl3}:}::{zahl2}", 1, 2, 3) + "\n");
        System.out.println("\t" + compositor.composeMessage("\\{nichtZahl1}\\\\{zahl1}", 1) + "\n");

        // with Flags
        System.out.println("\t" + compositor.composeMessage("Player {player:someflag:2flags:3rd>flag} not found!", "<playername>") + "\n");

        // TODO Date test
    }
}
