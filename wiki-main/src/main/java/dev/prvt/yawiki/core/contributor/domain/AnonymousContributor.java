package dev.prvt.yawiki.core.contributor.domain;

import dev.prvt.yawiki.common.util.jpa.converter.InetAddressConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.*;
import java.net.InetAddress;
import java.util.UUID;

@Entity
@Getter
@DiscriminatorValue("anon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "contributor_anon",
        indexes = {
                @Index(
                        name = "idx__contributor_anon__ip_address",
                        columnList = "ip_address",
                        unique = true
                )
        }
)
public class AnonymousContributor extends Contributor {
    @Column(name = "ip_address", unique = true, updatable = false, nullable = false)
    @Convert(converter = InetAddressConverter.class)
    private InetAddress ipAddress;
    @Override
    public String getName() {
        return ipAddress.getHostAddress();
    }
    @Builder
    public AnonymousContributor(@NotNull UUID id,
                                @NotNull InetAddress ipAddress) {
        super(id, ContributorState.NORMAL);
        this.ipAddress = ipAddress;
    }
}
