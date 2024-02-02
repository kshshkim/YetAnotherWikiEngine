package dev.prvt.yawiki.auth.authprocessor.config;

import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader;
import dev.prvt.yawiki.auth.authprocessor.domain.UsernamePasswordAuthenticator;
import dev.prvt.yawiki.auth.authprocessor.infra.TokenPayloadLoaderEssentialImpl;
import dev.prvt.yawiki.auth.authprocessor.infra.UsernamePasswordAuthenticatorMonolithicImpl;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import dev.prvt.yawiki.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthProcessorConfig {

    @Bean
    public TokenPayloadLoader tokenPayloadLoader(
        @Value("${yawiki.jwt.auth.access-token.lifespan}") int lifespan,
        @Value("${yawiki.jwt.auth.access-token.issuer}") String issuer,
        @Qualifier("tokenPayloadLoaderCurrentTimeProvider") CurrentTimeProvider tokenPayloadLoaderCurrentTimeProvider
    ) {
        return new TokenPayloadLoaderEssentialImpl(tokenPayloadLoaderCurrentTimeProvider, lifespan, issuer);
    }

    @Bean
    public UsernamePasswordAuthenticator usernamePasswordAuthenticator(
        MemberService memberService
    ) {
        return new UsernamePasswordAuthenticatorMonolithicImpl(memberService);
    }

}
