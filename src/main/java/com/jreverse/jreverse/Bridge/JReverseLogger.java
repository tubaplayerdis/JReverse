package com.jreverse.jreverse.Bridge;

public class JReverseLogger {
    static void Print(String message){
        System.out.println(message);
    }

    static void RemoveAndPrint(String message)
    {
        System.out.print((char) 8); // Intellij Specific
        System.out.println(message);
    }
    public static int PipeCallBackLimit = 2000;
}
