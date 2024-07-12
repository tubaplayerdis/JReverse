package com.jreverse.jreverse.Utils;

public class JReverseVersion {
    public String name;
    public float version;
    public String date;
    public int size;
    public String downloadLink;
    public boolean isdev;
    public JReverseVersion(String name, float version, String date, int size, String downloadLink, boolean isdev) {
        this.name = name;
        this.version = version;
        this.date = date;
        this.size = size;
        this.downloadLink = downloadLink;
        this.isdev = isdev;
    }
}
