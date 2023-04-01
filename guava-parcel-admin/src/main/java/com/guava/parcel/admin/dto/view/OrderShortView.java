package com.guava.parcel.admin.dto.view;

import com.guava.parcel.admin.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OrderShortView {
    private UUID id;
    private UUID userId;
    private Status status;
    private Instant updatedAt;
    private Instant createdAt;
}
