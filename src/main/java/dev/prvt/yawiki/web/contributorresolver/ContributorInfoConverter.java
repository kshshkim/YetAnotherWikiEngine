package dev.prvt.yawiki.web.contributorresolver;

import org.springframework.security.core.Authentication;

public interface ContributorInfoConverter {
    boolean supports(Authentication authentication);

    ContributorInfoArg convert(Authentication source);
}
