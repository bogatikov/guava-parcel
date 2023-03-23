package com.guava.parcel.admin.dto.form;

import com.guava.parcel.admin.model.Status;

import java.util.UUID;

public record ChangeOrderStatusForm(
        UUID orderId,
        Status status
) {
}
