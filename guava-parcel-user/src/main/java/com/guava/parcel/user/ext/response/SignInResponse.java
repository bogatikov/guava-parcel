package com.guava.parcel.user.ext.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {
}
