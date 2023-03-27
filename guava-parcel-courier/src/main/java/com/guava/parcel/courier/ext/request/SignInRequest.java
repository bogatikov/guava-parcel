package com.guava.parcel.courier.ext.request;

public record SignInRequest(
        String email,
        String password
) {
}
