package com.guava.parcel.admin.ext.response;

import com.guava.parcel.admin.model.UserType;

public record UserResponse(
        String lastName,
        String firstName,
        String email,
        UserType userType
) {
}
