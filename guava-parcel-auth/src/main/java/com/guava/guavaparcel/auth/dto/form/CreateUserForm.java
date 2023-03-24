package com.guava.guavaparcel.auth.dto.form;

import com.guava.guavaparcel.auth.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserForm(
        @NotBlank
        String lastName,
        @NotBlank
        String firstName,
        @NotBlank
        String email,
        @NotNull
        User.UserType userType
) {
}
