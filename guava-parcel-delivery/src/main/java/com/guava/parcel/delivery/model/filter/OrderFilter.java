package com.guava.parcel.delivery.model.filter;

import com.guava.parcel.delivery.model.Order;

import java.util.UUID;

public record OrderFilter(UUID userId, UUID courierId, Order.Status status) {
}
