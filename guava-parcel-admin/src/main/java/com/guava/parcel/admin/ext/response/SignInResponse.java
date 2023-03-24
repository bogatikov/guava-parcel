package com.guava.parcel.admin.ext.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {
}
