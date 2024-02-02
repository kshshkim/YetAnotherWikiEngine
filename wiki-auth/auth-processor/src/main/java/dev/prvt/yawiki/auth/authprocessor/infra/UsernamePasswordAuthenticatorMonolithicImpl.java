package dev.prvt.yawiki.auth.authprocessor.infra;

import dev.prvt.yawiki.auth.authprocessor.domain.UsernamePasswordAuthenticator;
import dev.prvt.yawiki.auth.authprocessor.exception.AuthenticationException;
import dev.prvt.yawiki.member.application.MemberPasswordVerificationData;
import dev.prvt.yawiki.member.application.MemberService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회원 서비스를 별도의 독립 애플리케이션으로 실행하지 않고, 직접 참조하는 구현.
 */
@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticatorMonolithicImpl implements UsernamePasswordAuthenticator {

    private final MemberService memberService;

    @Override
    public UUID authenticate(String username, String password) throws AuthenticationException {
        try {
            return memberService.verifyPassword(
                new MemberPasswordVerificationData(username, password)
            );
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}
