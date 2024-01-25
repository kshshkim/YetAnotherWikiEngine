package dev.prvt.yawiki.titleexistence.cache.exception;

public class CacheNotInitializedException extends IllegalStateException {

    public CacheNotInitializedException() {
    }

    public CacheNotInitializedException(String s) {
        super(s);
    }
}
