package com.jreverse.jreverse.Bridge;

public class JReverseBridge {
    public static native boolean testMethod();

    public static native int InjectDLL(int PID, String path);

    public static native void SetupPipe();

    public static native void PrimeLoadedClasses();

    public static native String GetStringPipe();
    public static native void WriteStringPipe(String message);

    public static native String[] CallCoreFunction(String name);

    // Load the native library
    static {
        System.loadLibrary("JReverseBridge");
    }

    // Main method
    public static void main(String[] args) {
        System.out.println(testMethod());
    }
}
