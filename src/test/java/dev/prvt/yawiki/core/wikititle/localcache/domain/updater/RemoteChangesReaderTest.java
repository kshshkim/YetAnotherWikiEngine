package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteReadCursorProvider.ReadCursor;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoteChangesReaderTest {

    @Mock
    RemoteChangesRepository remoteChangesRepository;

    @Mock
    RemoteReadCursorProvider remoteReadCursorProvider;

    RemoteChangesReader remoteChangesReader;

    @BeforeEach
    void init() {
        remoteChangesReader = new RemoteChangesReader(
            remoteReadCursorProvider,
            remoteChangesRepository
        );
    }

    @Test
    void readUpdated() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = now.minusMinutes(60);
        LocalDateTime before = now.plusMinutes(60);

        ReadCursor givenCursor = new ReadCursor(
            after, before
        );

        List<RemoteChangeLog> expected = List.of(
            new RemoteChangeLog(
                new WikiPageTitle(randString(), Namespace.NORMAL),
                LocalDateTime.now(),
                TitleUpdateType.CREATED
            )
        );


        given(remoteReadCursorProvider.getReadCursor())
            .willReturn(givenCursor);

        given(remoteChangesRepository.findRemoteChangesByCursor(eq(after), eq(before)))
            .willReturn(expected);

        // when
        List<RemoteChangeLog> actual = remoteChangesReader.readUpdated();

        // then
        assertThat(actual)
            .containsExactlyElementsOf(expected);
    }
}