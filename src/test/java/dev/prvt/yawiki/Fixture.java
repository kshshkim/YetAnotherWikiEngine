package dev.prvt.yawiki;

import net.bytebuddy.utility.RandomString;

public class Fixture {
    public static String randString() {
        return RandomString.make();
    }
}
