package com.guava.guavaparcel.dto.form;

import java.util.UUID;

public record ChangeDestinationForm(
        UUID orderId,
        String destinationAddress
) {
}
