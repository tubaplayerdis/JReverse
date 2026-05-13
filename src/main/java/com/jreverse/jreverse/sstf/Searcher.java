package com.jreverse.jreverse.sstf;

public class Searcher {
    public static String findMemberData(String name, Member[] members) {
        for(Member member : members) {
            if(name.equals(member.Name)) return member.Data;
        }
        return "null";
    }

    //Add find by file and find by whatever

}
