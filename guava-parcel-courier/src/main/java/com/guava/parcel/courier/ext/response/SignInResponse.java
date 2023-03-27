package com.guava.parcel.courier.ext.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {
}
