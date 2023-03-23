package com.guava.parcel.admin.ext.request;

import com.guava.parcel.admin.model.Status;

import java.util.UUID;

public record ChangeOrderStatusRequest(
        UUID orderId,
        Status status
) {
}
