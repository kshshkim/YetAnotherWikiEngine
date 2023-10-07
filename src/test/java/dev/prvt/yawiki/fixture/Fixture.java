package dev.prvt.yawiki.fixture;

import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;

import java.net.InetAddress;
import java.util.Random;

public class Fixture {
    public static String randString() {
        return RandomString.make();
    }

    @SneakyThrows
    public static InetAddress aInetV4Address() {
        Random random = new Random();
        byte[] ip = new byte[4];
        random.nextBytes(ip);
        return InetAddress.getByAddress(ip);
    }

}
