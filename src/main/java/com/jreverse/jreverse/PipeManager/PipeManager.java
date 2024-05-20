package com.jreverse.jreverse.PipeManager;

public class PipeManager {
    public static native void InitAPI();
    public static native String[] GetLoadedPipes();

    public static native String[] GetPipeInfo(String name);

    public static native void AddPipe(String name, int Size, String type);

    public static native void RemovePipe(String name);

    public static native void ResizeAndReconnectPipe(String name, int Size);
}
