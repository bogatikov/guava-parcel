package com.guava.guavaparcel.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
public class Order implements Persistable<UUID> {
    @Id
    @NonNull
    @Column("id")
    private UUID id;
    @Column("source_address")
    private String sourceAddress;
    @Column("destination_address")
    private String destinationAddress;
    @Column("user_id")
    private UUID userId;
    @Column("courier_id")
    private UUID courierId;
    @Column("status")
    private Status status;
    @Column("updated_at")
    private Instant updatedAt = null;
    @Column("created_at")
    @NonNull
    private Instant createdAt = Instant.now();
    @Version
    @Column("version")
    @NonNull
    private Long version;

    @Transient
    private Boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public enum Status {
        NEW,
        WAITING_FOR_COURIER,
        DELIVERING,
        FINISHED,
        CANCELED
    }
}
