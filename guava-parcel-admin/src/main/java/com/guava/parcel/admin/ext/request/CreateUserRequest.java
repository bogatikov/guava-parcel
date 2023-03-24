package com.guava.parcel.admin.ext.request;


import com.guava.parcel.admin.model.UserType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank
        String lastName,
        @NotBlank
        String firstName,
        @NotBlank
        String email,
        @NotNull
        UserType userType
) {
}
