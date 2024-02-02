package dev.prvt.yawiki.auth.member.api;

import dev.prvt.yawiki.member.application.MemberData;
import dev.prvt.yawiki.member.application.MemberJoinData;
import dev.prvt.yawiki.member.application.MemberPasswordUpdateData;
import dev.prvt.yawiki.member.application.MemberPasswordVerificationData;
import dev.prvt.yawiki.member.application.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    public record JoinRequest(
        @NotBlank(message = "Username must not be empty")
        @Pattern(regexp = "^[a-z].*", message = "Username must start with a lowercase Roman letter")
        @Size(min = 4, message = "Username must be at least 4 characters long")
        String username,

        @NotBlank(message = "Password must not be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
    ) {

        public MemberJoinData convert() {
            return new MemberJoinData(username, password);
        }
    }

    @PostMapping
    public MemberData join(
        @Validated @RequestBody JoinRequest request
    ) {
        return memberService.join(request.convert());
    }

    public record PasswordChangeRequest(
        @NotBlank(message = "Username must not be empty")
        @Pattern(regexp = "^[a-z].*", message = "Username must start with a lowercase Roman letter")
        @Size(min = 4, message = "Username must be at least 4 characters long")
        String username,

        @NotBlank(message = "Old password must not be empty")
        @Size(min = 8, message = "Old password must be at least 8 characters long")
        String oldPassword,

        @NotBlank(message = "New password must not be empty")
        @Size(min = 8, message = "New password must be at least 8 characters long")
        String newPassword
    ) {

        public MemberPasswordUpdateData convert() {
            return new MemberPasswordUpdateData(username, oldPassword, newPassword);
        }
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
        @Validated @RequestBody PasswordChangeRequest request
    ) {
        memberService.updatePassword(request.convert());
    }

    public record PasswordVerificationRequest(
        @NotBlank(message = "Username must not be empty")
        @Pattern(regexp = "^[a-z].*", message = "Username must start with a lowercase Roman letter")
        @Size(min = 4, message = "Username must be at least 4 characters long")
        String username,

        @NotBlank(message = "Password must not be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
    ) {

        public MemberPasswordVerificationData convert() {
            return new MemberPasswordVerificationData(username, password);
        }
    }

}
