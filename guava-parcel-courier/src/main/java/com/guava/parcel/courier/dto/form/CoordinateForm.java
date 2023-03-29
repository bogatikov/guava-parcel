package com.guava.parcel.courier.dto.form;

import javax.validation.constraints.NotNull;

public record CoordinateForm(
        @NotNull
        Double longitude,
        @NotNull
        Double latitude
) {
}
