package com.guava.guavaparcel.dto.view;

import java.util.UUID;

public record ChangeDestinationView(
        UUID id,
        String destinationAddress
) {
}
