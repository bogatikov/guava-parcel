package com.guava.parcel.courier.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInForm(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
