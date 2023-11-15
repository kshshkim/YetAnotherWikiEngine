//package dev.prvt.yawiki.auth.member.application;
//
//import dev.prvt.yawiki.core.event.MemberJoinEvent;
//import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
//import dev.prvt.yawiki.core.contributor.domain.Contributor;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationEventPublisher;
//
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Slf4j
//public class MemberJoinEventTest {
//    @Autowired
//    ApplicationEventPublisher publisher;
//
//    @Autowired
//    DefaultPermissionProperties defaultPermissionProperties;
//
//    @Autowired
//    EntityManager em;
//
//    // todo 테스트 결합도 줄이기
//    @Test
//    @Transactional
//    public void should_persist_entities() {
//        MemberJoinEvent memberJoinEvent = new MemberJoinEvent(UUID.randomUUID(), "hello");
//        publisher.publishEvent(memberJoinEvent);
//
//        Contributor contributor = em.find(Contributor.class, memberJoinEvent.memberId());
//        AuthorityProfile authorityProfile = em.find(AuthorityProfile.class, memberJoinEvent.memberId());
//
//        assertThat(contributor)
//                .isNotNull();
//        assertThat(authorityProfile)
//                .isNotNull();
//    }
//}
