package com.guava.parcel.user.dto.form;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignInForm(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
