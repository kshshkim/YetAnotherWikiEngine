package dev.prvt.yawiki.web.contributorresolver.converters;

import dev.prvt.yawiki.core.contributor.application.ContributorApplicationService;
import dev.prvt.yawiki.core.contributor.application.ContributorData;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnonymousAuthenticationTokenContributorInfoConverter implements ContributorInfoConverter {
    private final ContributorApplicationService contributorApplicationService;

    @Override
    public boolean supports(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @SneakyThrows
    @Override
    public ContributorInfoArg convert(Authentication source) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) source.getDetails();
        ContributorData contributor = contributorApplicationService.getContributorByIpAddress(InetAddress.getByName(details.getRemoteAddress()));
        return new ContributorInfoArg(contributor.getContributorId());
    }
}
