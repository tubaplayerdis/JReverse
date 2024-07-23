package com.jreverse.jreverse.Bridge;

public class JReverseBridge {
    public static final String[] NoneArg = {"NONE"};
    public static native boolean testMethod();

    public static native void InitBridge();

    public static native int StartAndInjectDLL(String path, String app);
    public static native int InjectDLL(int PID, String path);

    public static native int WriteStartupPipe(Object[] rules, Object settings);

    public static native void SetupPipe();

    public static native void SetCurrentClassBytes(Byte[] arg);

    public static native void PrimeLoadedClasses();

    public static native String GetStringPipe();
    public static native void WriteStringPipe(String message);

    public static native String[] CallCoreFunction(String name, String[] args);

    public static native float GetVersion();

    public static native void  ReloadPipes();

    public static native float GetCoreFileVersion(String location);

    // Load the native library
    static {
        System.loadLibrary("JReverseBridge");
    }

    // Main method
    public static void main(String[] args) {
        System.out.println(testMethod());
    }
}
