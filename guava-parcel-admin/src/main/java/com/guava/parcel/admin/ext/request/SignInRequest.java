package com.guava.parcel.admin.ext.request;

public record SignInRequest(
        String email,
        String password
) {
}
