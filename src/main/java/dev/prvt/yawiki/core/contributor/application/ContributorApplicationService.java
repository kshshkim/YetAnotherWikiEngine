package dev.prvt.yawiki.core.contributor.application;

import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContributorApplicationService {
    private final ContributorRepository contributorRepository;

    public ContributorData getContributorByIpAddress(InetAddress inetAddress) {
        return ContributorData.from(contributorRepository.getByInetAddress(inetAddress));
    }
}
