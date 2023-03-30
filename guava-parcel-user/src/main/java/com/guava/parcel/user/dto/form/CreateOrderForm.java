package com.guava.parcel.user.dto.form;

import javax.validation.constraints.NotNull;

public record CreateOrderForm(
        @NotNull
        String sourceAddress,
        @NotNull
        String destinationAddress
) {
}
