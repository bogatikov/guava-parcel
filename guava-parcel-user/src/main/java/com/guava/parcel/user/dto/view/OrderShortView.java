package com.guava.parcel.user.dto.view;


import com.guava.parcel.user.model.Status;
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
    UUID id;
    UUID userId;
    Status status;
    Instant updatedAt;
    Instant createdAt;
}
