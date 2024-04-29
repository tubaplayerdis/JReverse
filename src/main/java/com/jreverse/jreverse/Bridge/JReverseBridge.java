package com.jreverse.jreverse.Bridge;

import com.jreverse.jreverse.StartupRule;

public class JReverseBridge {
    public static final String[] NoneArg = {"NONE"};
    public static native boolean testMethod();

    public static native void InitBridge();

    public static native int StartAndInjectDLL(String path, String app);
    public static native int InjectDLL(int PID, String path);

    public static native int WriteStartupPipe(StartupRule[] rules);

    public static native void SetupPipe();

    public static native void SetCurrentClassBytes(Byte[] arg);

    public static native void PrimeLoadedClasses();

    public static native String GetStringPipe();
    public static native void WriteStringPipe(String message);

    public static native String[] CallCoreFunction(String name, String[] args);

    // Load the native library
    static {
        System.loadLibrary("JReverseBridge");
    }

    // Main method
    public static void main(String[] args) {
        System.out.println(testMethod());
    }
}
