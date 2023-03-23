package com.guava.guavaparcel.auth.model;

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

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User implements Persistable<UUID> {
    @Id
    @Column("id")
    @NonNull
    private UUID id = UUID.randomUUID();
    @Column("email")
    @NonNull
    private String email;
    @Column("last_name")
    @NonNull
    private String lastName;
    @Column("first_name")
    @NonNull
    private String firstName;
    @Column("password_hash")
    @NonNull
    private String passwordHash;
    @Column("user_type")
    @NonNull
    private UserType userType;
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

    public enum UserType {
        USER,
        COURIER,
        ADMIN
    }
}