package dev.prvt.yawiki.auth;

import dev.prvt.yawiki.web.rest.schema.ErrorMessage;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice(basePackages = {"dev.prvt.yawiki.auth"})
public class ControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage requestArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessageFrom = getErrorMessageFrom(ex);
        return new ErrorMessage(HttpStatus.BAD_REQUEST, errorMessageFrom, request.getDescription(false));
    }

    private String getErrorMessageFrom(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                   .getFieldErrors()
                   .stream()
                   .map(DefaultMessageSourceResolvable::getDefaultMessage)
                   .collect(Collectors.joining(", "));
    }

}
