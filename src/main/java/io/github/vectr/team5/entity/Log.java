package io.github.vectr.team5.entity;

import io.github.vectr.team5.enums.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "log")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    public Log(Device device, EventType eventType, LocalDateTime detectedAt) {
        this.device = device;
        this.eventType = eventType;
        this.detectedAt = detectedAt;
    }
}