package com.guava.guavaparcel.model.filter;

import com.guava.guavaparcel.model.Order;

import java.util.UUID;

public record OrderFilter(UUID userId, UUID courierId, Order.Status status) {
}
