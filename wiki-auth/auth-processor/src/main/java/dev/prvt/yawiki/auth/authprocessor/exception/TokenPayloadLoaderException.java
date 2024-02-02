package dev.prvt.yawiki.auth.authprocessor.exception;

public class TokenPayloadLoaderException extends AuthProcessorException {

    public TokenPayloadLoaderException() {
        super();
    }

    public TokenPayloadLoaderException(String message) {
        super(message);
    }

    public TokenPayloadLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenPayloadLoaderException(Throwable cause) {
        super(cause);
    }

}
