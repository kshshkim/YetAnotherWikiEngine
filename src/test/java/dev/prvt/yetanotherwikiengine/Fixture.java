package dev.prvt.yetanotherwikiengine;

import net.bytebuddy.utility.RandomString;

public class Fixture {
    public static String randString() {
        return RandomString.make();
    }
}
