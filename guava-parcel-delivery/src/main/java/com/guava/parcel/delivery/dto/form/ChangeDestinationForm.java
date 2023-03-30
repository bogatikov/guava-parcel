package com.guava.parcel.delivery.dto.form;

import java.util.UUID;

public record ChangeDestinationForm(
        UUID orderId,
        String destinationAddress
) {
}
