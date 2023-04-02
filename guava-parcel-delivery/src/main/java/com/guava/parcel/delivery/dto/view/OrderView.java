package com.guava.parcel.delivery.dto.view;

import com.guava.parcel.delivery.model.Order;
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
public class OrderView {
    private UUID id;
    private UUID userId;
    private UUID courierId;
    private String sourceAddress;
    private String destinationAddress;
    private Order.Status status;
    private Instant updatedAt;
    private Instant createdAt;
}
