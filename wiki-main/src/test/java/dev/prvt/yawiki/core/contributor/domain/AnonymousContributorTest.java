package dev.prvt.yawiki.core.contributor.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.aInetV4Address;
import static org.assertj.core.api.Assertions.assertThat;

class AnonymousContributorTest {
    UUID givenId;
    AnonymousContributor givenAnonymousContributor;
    InetAddress givenInetAddress;
    @BeforeEach
    void init() {
        givenId = UUID.randomUUID();
        givenInetAddress = aInetV4Address();
        givenAnonymousContributor = AnonymousContributor.builder()
                .id(givenId)
                .ipAddress(givenInetAddress)
                .build();
    }

    @Test
    void getName() {
        String name = givenAnonymousContributor.getName();
        assertThat(name)
                .isEqualTo(givenInetAddress.getHostAddress());
    }

    @Test
    void getIpAddress() {
        assertThat(givenAnonymousContributor.getIpAddress())
                .isEqualTo(givenInetAddress);
    }

    @Test
    void getState_default() {
        assertThat(givenAnonymousContributor.getState())
                .isEqualTo(ContributorState.NORMAL);
    }
}