package com.guava.parcel.user.ext.response;


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
public class OrderShortResponse {
    private UUID id;
    private UUID userId;
    private Status status;
    private Instant updatedAt;
    private Instant createdAt;
}
