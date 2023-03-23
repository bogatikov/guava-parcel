package com.guava.parcel.admin.dto.form;

import com.guava.parcel.admin.model.Status;
import org.springframework.lang.NonNull;

import java.util.UUID;

public record ChangeOrderStatusForm(
        @NonNull
        UUID orderId,
        @NonNull
        Status status
) {
}
