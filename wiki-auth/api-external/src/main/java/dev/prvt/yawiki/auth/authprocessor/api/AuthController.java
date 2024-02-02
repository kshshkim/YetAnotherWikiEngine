package dev.prvt.yawiki.auth.authprocessor.api;

import dev.prvt.yawiki.auth.authprocessor.application.AuthProcessorService;
import dev.prvt.yawiki.auth.authprocessor.exception.AuthenticationException;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenException;
import dev.prvt.yawiki.web.rest.schema.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthProcessorService authProcessorService;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, RefreshTokenException.class})
    public ErrorMessage authFailed(WebRequest request) {
        return new ErrorMessage(HttpStatus.UNAUTHORIZED, "authentication.failed", request.getContextPath());
    }


    public record PasswordAuthRequest(
        @NotBlank(message = "Username cannot be empty")
        String username,

        @NotBlank(message = "Password cannot be empty")
        String password
    ) {

    }

    @PostMapping("/login")
    public AuthToken usernamePasswordAuth(
        @Validated @RequestBody PasswordAuthRequest request
    ) {
        return authProcessorService.usernamePasswordAuth(request.username(), request.password());
    }

    public record RefreshTokenRenewRequest(
        @NotBlank(message = "Username cannot be empty")
        String username,

        @NotBlank(message = "Refresh token cannot be empty")
        String refreshToken
    ) {

    }

    @PostMapping("/refresh")
    public AuthToken refreshTokenAuth(
        @Validated @RequestBody RefreshTokenRenewRequest request
    ) {
        return authProcessorService.refreshTokenAuth(request.username(), request.refreshToken());
    }
}
