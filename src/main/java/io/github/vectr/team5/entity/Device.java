package io.github.vectr.team5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(
        name = "device",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_device_device_identifier",
                        columnNames = "device_identifier"
                ),
                @UniqueConstraint(
                        name = "uq_device_user_alias",
                        columnNames = {"user_id", "alias"}
                )
        }
)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "device_identifier", nullable = false, length = 50)
    private String deviceIdentifier;

    @Column(name = "alias", length = 20)
    private String alias;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Device(String userId, String deviceIdentifier, String alias) {
        this.userId = userId;
        this.deviceIdentifier = deviceIdentifier;
        this.alias = alias;
        this.registeredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void register(String keycloakSub, String alias) {
        this.userId = keycloakSub;
        this.alias = alias;
        this.registeredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void unregister() {
        this.userId = null;
        this.alias = null;
        this.registeredAt = null;
    }

    public void updateAlias(String alias) {
        this.alias = alias;
    }

    public void updateUserId(String userId) {
        this.userId = userId;
    }
}