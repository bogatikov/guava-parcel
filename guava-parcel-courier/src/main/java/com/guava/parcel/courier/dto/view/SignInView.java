package com.guava.parcel.courier.dto.view;

public record SignInView(
        String accessToken,
        String refreshToken
) {
}
