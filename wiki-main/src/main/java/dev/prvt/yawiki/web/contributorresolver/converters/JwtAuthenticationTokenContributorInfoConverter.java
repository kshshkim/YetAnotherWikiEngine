package dev.prvt.yawiki.web.contributorresolver.converters;

import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenContributorInfoConverter implements ContributorInfoConverter {
    @Override
    public boolean supports(Authentication authentication) {
        return authentication instanceof JwtAuthenticationToken;
    }

    @Override
    public ContributorInfoArg convert(Authentication source) {
        JwtAuthenticationToken jwtSource = (JwtAuthenticationToken) source;
        String contributorId = (String) jwtSource.getTokenAttributes().get("contributorId");
        return new ContributorInfoArg(UUID.fromString(contributorId));
    }
}
