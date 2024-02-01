package dev.prvt.yawiki.member.exception;

public class NoSuchMemberException extends MemberException {
    public NoSuchMemberException() {
        super();
    }

    public NoSuchMemberException(String message) {
        super(message);
    }

    public NoSuchMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchMemberException(Throwable cause) {
        super(cause);
    }
}
