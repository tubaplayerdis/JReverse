package com.jreverse.jreverse.sstf;

public class Member {
    public Member() {
        Name = "null";
        Data = "null";
    }
    public Member(String name, String data) {
        Name = name;
        Data = data;
    }
    public enum NullTypes {
        NAME_NULL,
        DATA_NULL,
        ALL_NULL,
        NONE_NULL
    }
    public NullTypes CheckNull() {
        if(Name.equals("null") && Data.equals("null")) return NullTypes.ALL_NULL;
        if(Name.equals("null")) return NullTypes.NAME_NULL;
        if(Data.equals("null")) return NullTypes.DATA_NULL;
        return NullTypes.NONE_NULL;
    }
    public String Name;
    public String Data;
}
