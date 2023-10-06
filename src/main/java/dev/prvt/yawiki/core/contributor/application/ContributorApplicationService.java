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

    /**
     * 해당 ip 주소를 가진 익명 contributor가 db에 존재하지 않으면 엔티티를 새로 생성하고 영속화해서 반환함.
     * @param inetAddress
     * @return
     */
    @Transactional
    public ContributorData getContributorByIpAddress(InetAddress inetAddress) {
        return ContributorData.from(contributorRepository.getByInetAddress(inetAddress));
    }
}
