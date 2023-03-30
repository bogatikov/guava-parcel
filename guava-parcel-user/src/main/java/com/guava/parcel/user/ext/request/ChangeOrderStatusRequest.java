package com.guava.parcel.user.ext.request;



import com.guava.parcel.user.model.Status;

import java.util.UUID;

public record ChangeOrderStatusRequest(
        UUID orderId,
        Status status
) {
}
