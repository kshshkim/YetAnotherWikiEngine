package dev.prvt.yawiki.core.wikititle.localcache.exception;

public class CacheNotInitializedException extends IllegalStateException {

    public CacheNotInitializedException() {
    }

    public CacheNotInitializedException(String s) {
        super(s);
    }
}
