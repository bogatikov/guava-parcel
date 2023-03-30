package com.guava.parcel.user.dto.form;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record ChangeDestinationForm(
        @NotNull
        UUID orderId,
        @NotNull
        String destinationAddress
) {
}
