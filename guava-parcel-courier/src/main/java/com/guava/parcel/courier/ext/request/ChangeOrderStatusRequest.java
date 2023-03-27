package com.guava.parcel.courier.ext.request;


import com.guava.parcel.courier.model.Status;

import java.util.UUID;

public record ChangeOrderStatusRequest(
        UUID orderId,
        Status status
) {
}
