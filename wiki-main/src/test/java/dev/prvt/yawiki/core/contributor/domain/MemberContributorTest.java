package dev.prvt.yawiki.core.contributor.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class MemberContributorTest {
    MemberContributor givenMemberContributor;
    String givenMemberName;
    UUID givenId;

    @BeforeEach
    void init() {
        givenMemberName = randString();
        givenId = UUID.randomUUID();
        givenMemberContributor = MemberContributor.builder()
                .memberName(givenMemberName)
                .id(givenId)
                .build();
    }

    @Test
    void getName_test() {
        assertThat(givenMemberContributor.getName())
                .isEqualTo(givenMemberName);
    }

    @Test
    void getStatus_default() {
        assertThat(givenMemberContributor.getState())
                .isEqualTo(ContributorState.NORMAL);
    }
}