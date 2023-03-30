package com.guava.parcel.user.ext.request;

public record SignInRequest(
        String email,
        String password
) {
}
