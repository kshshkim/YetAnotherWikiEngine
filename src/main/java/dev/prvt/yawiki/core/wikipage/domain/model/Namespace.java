package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.Getter;

@Getter
public enum Namespace {
    NORMAL(1, "normal"),
    FILE(3, "file"),
    TEMPLATE(5, "template"),
    CATEGORY(7, "category"),
    MAIN(9, "main")
    ;

    private final int intValue;
    private final String stringValue;

    Namespace(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }
}
