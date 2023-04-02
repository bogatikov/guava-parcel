package com.guava.parcel.courier.ext.response;


import com.guava.parcel.courier.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode(of = {"id"})
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private UUID courierId;
    private String sourceAddress;
    private String destinationAddress;
    private Status status;
    private Instant updatedAt;
    private Instant createdAt;
}
