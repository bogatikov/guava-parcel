package com.guava.parcel.courier.dto.form;


import com.guava.parcel.courier.model.Status;

import java.util.UUID;

public record ChangeOrderStatusForm(
        UUID orderId,
        Status status
) {
}
