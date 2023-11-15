package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.event.MemberJoinEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberJoinEventHandlerTest {

    @Mock
    AuthorityProfileCommandService mockService;

    @InjectMocks
    MemberJoinEventHandler memberJoinEventHandler;

    @Test
    void handle() {
        // given
        MemberJoinEvent givenEvent = new MemberJoinEvent(UUID.randomUUID(), UUID.randomUUID().toString());

        // when
        memberJoinEventHandler.handle(givenEvent);

        // then
        verify(mockService).createAuthorityProfile(givenEvent.memberId());
    }
}