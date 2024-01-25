package dev.prvt.yawiki.common.model;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Namespace {

    NORMAL(1, "normal"),
    FILE(3, "file"),
    TEMPLATE(5, "template"),
    CATEGORY(7, "category"),
    MAIN(9, "main")
    ;
    private static final Namespace[] namespaces = new Namespace[255];
    static {
        Arrays.stream(Namespace.values())
            .forEach(namespace -> namespaces[namespace.getIntValue()] = namespace);
    }

    public static Namespace valueOf(int intValue) {
        return namespaces[intValue];
    }

    private final int intValue;
    private final String stringValue;

    Namespace(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }
}
