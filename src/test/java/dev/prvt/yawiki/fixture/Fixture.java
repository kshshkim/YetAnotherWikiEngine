package dev.prvt.yawiki.fixture;

import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Fixture {

    private static final int RANDOM_STRING_DEFAULT_LENGTH = 8;

    public static String randString() {
        return new RandomString(RANDOM_STRING_DEFAULT_LENGTH, random()).nextString();
    }

    public static String randString(int length) {
        validateLength(length);
        return new RandomString(length, random()).nextString();
    }

    public static String randString(int minLength, int maxLength) {
        validateLength(minLength);
        validateLength(maxLength);
        if (minLength > maxLength) {
            throw new IllegalArgumentException("maxLength should be greater than minLength");
        }
        return randString(random().nextInt(minLength, maxLength + 1));
    }
    private static void validateLength(int length) {
        if (!(3 < length && length < 256)) {
            throw new IllegalArgumentException("length should be between 4 and 255");
        }
    }

    public static Random random() {
        return ThreadLocalRandom.current();
    }

    @SneakyThrows
    public static InetAddress aInetV4Address() {
        Random random = new Random();
        byte[] ip = new byte[4];
        random.nextBytes(ip);
        return InetAddress.getByAddress(ip);
    }

}
