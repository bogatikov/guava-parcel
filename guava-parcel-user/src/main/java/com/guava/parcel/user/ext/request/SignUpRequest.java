package com.guava.parcel.user.ext.request;

public record SignUpRequest(
        String lastName,
        String firstName,
        String email,
        String password
) {
}
